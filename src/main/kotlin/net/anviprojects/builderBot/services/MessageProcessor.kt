package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.repositories.PlaceholderRepository
import net.anviprojects.builderBot.repositories.TeamcityRepository
import net.anviprojects.builderBot.tasks.Task
import net.anviprojects.builderBot.tasks.TaskType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList


/*
 * Вспомогательный класс который разбирает входящий Message и на основе его формирует одну или список Task
 * Парсинг производится по следующим правилам:
 * каждое сообщение боту должно содержать в себе намерение и некоторое кол-во целей к которым его нужно применить
 * намерениями считаются - сборка, обновление, перезагрузка
 * целями считаются ветки - master, stable и сервера - masterStand, stableStand и т.д.
 * если приходит сообщение с намерением "сборка" - то будет создана задача с типом BUILD, следующее слово после него будет целью задачи
 * если после цели стоит "и" или ","  - то будет создана еще одна задача с типом BUILD и целью, которая указана после разделителя
 * если приходит сообщение с намерением "обновление" - то будет создана задача с типом DEPLOY
 *
 * Примеры:
 * Сборка master - формируется Task (master, TaskType.BUILD, User)
 * Сборка master и stable - формируется Task(master, TaskType.BUILD, User) и Task(stable, TaskType.BUILD, User)
 * Обновление masterStand - формируется Task(masterStand, TaskType.DEPLOY, User)
 *
 */
@Component
class MessageProcessor {

    @Autowired
    lateinit var placeholderRepository : PlaceholderRepository
    @Autowired
    lateinit var teamcityRepository: TeamcityRepository

    var buildPurposes = listOf("сборка", "build", "собери", "собрать")

    var deployPurposes = listOf("обнови", "обновить", "поставка", "обновление")

    val rebootPurposes = listOf("ребут", "ребутни", "перезагрузи")

    fun createTasks(message: String, user: BotUser): List<Task> {
        val buildToAliases = placeholderRepository.builds.map { it.name to it.aliases }.toMap()
        val serverToAliases = placeholderRepository.weblogics.map { it.weblogicAddress to it.aliases }.toMap()

        val tasks = mutableListOf<Task>()
        // вариант так себе - лишний раз проходим по ненужным спискам
        _createTasks(message, tasks, user, buildPurposes, buildToAliases, TaskType.BUILD)
        _createTasks(message, tasks, user, deployPurposes, serverToAliases, TaskType.DEPLOY)
        _createTasks(message, tasks, user, rebootPurposes, serverToAliases, TaskType.REBOOT)
        return tasks
    }

    //TODO функция так себе. переделать при первой же возможности
    private fun _createTasks(msgContent: String, tasks: MutableList<Task>, user: BotUser, purposes : List<String>,
                             goalToAliases : Map<String, List<String>>, taskType : TaskType) {
        for (buildP in purposes) {
            if (msgContent.toLowerCase().contains(buildP)) {
                val rawGoalsArray = msgContent.substringAfter(buildP).split(" ", ",")
                for (goal in rawGoalsArray) {
                    for (keyPair in goalToAliases) {
                        if (keyPair.value.contains(goal)) {
                            tasks.add(Task(keyPair.key, taskType, user))
                        }
                    }
                }
            }
        }
    }

    fun isYes(message: String) = message.equals("да") || message.equals("+")
    fun isNo(message: String) = message.equals("нет") || message.equals("-")

    fun isTaskMessage(content: String) = buildPurposes.stream().anyMatch { content.contains(it) }
            || deployPurposes.stream().anyMatch { content.contains(it) }
            || rebootPurposes.stream().anyMatch { content.contains(it) }

    fun isSystemMessage(message: String) = message.startsWith("!")
    fun isGetHelp(message: String): Boolean = message.equals("!help")
    fun isShutdown(message: String): Boolean = message.equals("!shutdown")
    private val s = "!add_teamcity"

    fun isAddTeamcity(message: String): Boolean = message.startsWith("!add_teamcity")
    fun isAddBuildPlan(message: String): Boolean = message.startsWith("!add_buildplan")
    fun isAddWebLogic(message: String): Boolean = message.startsWith("!add_weblogic")

    fun parseTeamcityMessage(message: String, botUser: BotUser): Teamcity? {
        // TODO сейчас botUser не используется. Нужно делать на него ссылку здесь и далее
        val msgArray = message.substringAfter("!add_teamcity ").split(" ")
        if (msgArray.size != 3) {
            return null // TODO возможно стоит заменить на выброс исключения и тогда возвращаемое значение будет null-safety
        }
        botUser.teamcityLogin = msgArray[1]
        botUser.teamcityPassword = msgArray[2]
        return placeholderRepository.saveOrUpdateTeamcity(Teamcity(msgArray[0]))
    }

    fun parseBuildPlanMessage(message: String, botUser: BotUser): BuildPlan? {
        val msgArray = message.substringAfter("!add_buildplan ").split(" ")
        if (msgArray.size < 2) {
            return null
        }
        val teamcity = teamcityRepository.findByBuildPlans_Name(msgArray[0])
        if (teamcity == null) return null

        if (msgArray.size == 2) {
            return placeholderRepository.saveOrUpdateBuild(BuildPlan(msgArray[0], teamcity, emptyList()))
        } else {
            // TODO получать тимсити из БД по имени
            return placeholderRepository.saveOrUpdateBuild(
                    BuildPlan(msgArray[0], teamcity,
                            message.substringAfter(msgArray[1]).split(",").stream().map(String::trim).toList()))
        }
    }

    fun parseWebLogicMessage(message: String, botUser: BotUser): WebLogic? {
        val msgArray = message.substringAfter("!add_weblogic ").trim().split(" ")
        if (msgArray.size < 3) {
            return null
        } else if (msgArray.size == 3) {
            return placeholderRepository.saveOrUpdateWeblogic(WebLogic(msgArray[0], msgArray[1], msgArray[2], mutableListOf<String>()))
        } else {
            val webLogic = WebLogic(msgArray[0], msgArray[1], msgArray[2],
                    message.substringAfter(msgArray[2]).split(",").stream().map(String::trim).toList() as MutableList<String>)
            return placeholderRepository.saveOrUpdateWeblogic(webLogic)
        }
    }

    fun isStart(message: String) = message.startsWith("!start")

}
