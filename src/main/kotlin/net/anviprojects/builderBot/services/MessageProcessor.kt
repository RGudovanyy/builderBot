package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.repositories.*
import net.anviprojects.builderBot.tasks.*
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.StringBuilder
import kotlin.streams.toList


/*
 * Вспомогательный класс который разбирает входящий Message и на основе его формирует одну или список AbstractTask
 * Парсинг производится по следующим правилам:
 * каждое сообщение боту должно содержать в себе намерение и некоторое кол-во целей к которым его нужно применить
 * намерениями считаются - сборка, обновление, перезагрузка
 * целями считаются ветки - master, stable и сервера - masterStand, stableStand и т.д.
 * если приходит сообщение с намерением "сборка" - то будет создана задача с типом BUILD, следующее слово после него будет целью задачи
 * если после цели стоит "и" или ","  - то будет создана еще одна задача с типом BUILD и целью, которая указана после разделителя
 * если приходит сообщение с намерением "обновление" - то будет создана задача с типом DEPLOY
 *
 * Примеры:
 * Сборка master - формируется AbstractTask (master, TaskType.BUILD, User)
 * Сборка master и stable - формируется AbstractTask(master, TaskType.BUILD, User) и AbstractTask(stable, TaskType.BUILD, User)
 * Обновление masterStand - формируется AbstractTask(masterStand, TaskType.DEPLOY, User)
 *
 */
@Component
class MessageProcessor {

    @Autowired
    lateinit var teamcityRepository: TeamcityRepository
    @Autowired
    lateinit var buildRepository: BuildRepository
    @Autowired
    lateinit var weblogicRepository: WeblogicRepository
    @Autowired
    lateinit var botUserRepository: BotUserRepository

    var buildPurposes = listOf("сборка", "build", "собери", "собрать")

    var deployPurposes = listOf("обнови", "обновить", "поставка", "обновление")

    val rebootPurposes = listOf("ребут", "ребутни", "перезагрузи")

    fun createTasks(message: String, user: BotUser, taskType: TaskType): Set<AbstractTask> {
        val tasks = mutableSetOf<AbstractTask>()

        when(taskType) {
            TaskType.BUILD -> createBuildTask(message, tasks, user, buildPurposes, buildRepository)
            TaskType.DEPLOY -> createDeployTask(message, tasks, user, deployPurposes, buildRepository, weblogicRepository)
            TaskType.REBOOT -> createRebootTask(message, tasks, user, rebootPurposes, weblogicRepository)
        }
        return tasks
    }

    private fun createRebootTask(message: String, tasks: MutableSet<AbstractTask>, user: BotUser, rebootPurposes: List<String>,
                                 weblogicRepository: WeblogicRepository) {
        for (buildP in rebootPurposes) {
            if (message.toLowerCase().contains(buildP)) {
                val rawGoalsArray = message.substringAfter(buildP).split(" ", ",")
                for (goal in rawGoalsArray) {
                    val weblogic = weblogicRepository.findByAlias(goal)
                    if (weblogic == null) {
                        throw RuntimeException("Не найден стенд с алиасом ${goal}")
                    }
                    tasks.add(RebootTask(weblogic, TaskType.DEPLOY, user))
                }
            }
        }
    }

    private fun createDeployTask(message: String, tasks: MutableSet<AbstractTask>, user: BotUser, deployPurposes: List<String>,
                                 buildRepository: BuildRepository, weblogicRepository: WeblogicRepository) {
        val buildPlans = buildRepository.findAllDeployBuildPlans()
        val deployAliasToBuild = buildPlans.stream().map { it!!.deployAlias to it }.toList()


        for (buildP in deployPurposes) {
            if (message.toLowerCase().contains(buildP)) {
                val rawGoalsArray = message.substringAfter(buildP).split(" ", ",")
                for (goal in rawGoalsArray) {
                    for (keyPair in deployAliasToBuild) {
                        if (keyPair.first.equals(goal)) {
                            val weblogic = weblogicRepository.findByAlias(keyPair.first!!)
                            if (weblogic == null) {
                                throw RuntimeException("Не найден стенд с алиасом ${keyPair.first}")
                            }
                            tasks.add(DeployTask(keyPair.second, weblogic, TaskType.DEPLOY, user))
                        }
                    }
                }
            }
        }
    }

    private fun createBuildTask(message: String, tasks: MutableSet<AbstractTask>, user: BotUser,
                                purposes: List<String>, buildRepository: BuildRepository) {
        val buildPlans = buildRepository.findAll()
        val aliasToBuild = buildPlans.stream().map { it.aliases.joinToString() to it }.toList()

        for (buildP in purposes) {
            if (message.toLowerCase().contains(buildP)) {
                val rawGoalsArray = message.substringAfter(buildP).split(" ", ",")
                for (goal in rawGoalsArray) {
                    for (keyPair in aliasToBuild) {
                        if (keyPair.first.contains(goal)) {
                            tasks.add(BuildTask(keyPair.second, TaskType.BUILD, user))
                        }
                    }
                }
            }
        }

    }

    fun isYes(message: String) = message.equals("да") || message.equals("+")
    fun isNo(message: String) = message.equals("нет") || message.equals("-")

    fun isSystemMessage(message: String) = message.startsWith("!")
    fun isGetHelp(message: String): Boolean = message.equals("!help")
    fun isShutdown(message: String): Boolean = message.equals("!shutdown")
    fun isAddTeamcity(message: String): Boolean = message.startsWith("!add_teamcity")
    fun isAddBuildPlan(message: String): Boolean = message.startsWith("!add_buildplan")
    fun isAddWebLogic(message: String): Boolean = message.startsWith("!add_weblogic")
    // TODO возвращать список, для возможности в одном сообщении задать несколько задач
    fun identifyTaskType(message: String) : TaskType? {
        if (buildPurposes.stream().anyMatch { message.contains(it) }) return TaskType.BUILD
        if (deployPurposes.stream().anyMatch { message.contains(it) }) return TaskType.DEPLOY
        if (rebootPurposes.stream().anyMatch { message.contains(it) }) return TaskType.DEPLOY
        return null
    }


    fun parseTeamcityMessage(message: String, botUser: BotUser): Teamcity? {
        var rawMessage = message.trim().substringAfter("!add_teamcity ")
        if (rawMessage.contains("href")) {
            rawMessage = excludeLinkFromMessage(rawMessage)
        }
        val msgArray = rawMessage.split(" ")
        if (msgArray.size != 3) {
            println("Некорректное число аргументов : ${msgArray}")
            return null // TODO возможно стоит заменить на выброс исключения и тогда возвращаемое значение будет null-safety
        }
        // записываем логин и пароль в сущность текущего пользователя
        botUser.teamcityLogin = msgArray[1]
        botUser.teamcityPassword = msgArray[2]
        botUserRepository.save(botUser)
        return teamcityRepository.save(Teamcity(msgArray[0]))
    }

    fun excludeLinkFromMessage(rawMessage: String): String {
        // some_text <a href>some_link</a> some_text -> some_text some_link some_text
        val stringBuilder  = StringBuilder()
        stringBuilder.append(rawMessage.substringBefore('<'))
        stringBuilder.append(StringUtils.substringBetween(rawMessage, ">", "</"))
        stringBuilder.append(rawMessage.substringAfterLast('>'))
        return stringBuilder.toString()
    }

    fun parseBuildPlanMessage(message: String, botUser: BotUser): BuildPlan? {
        var rawMessage = message.substringAfter("!add_buildplan ")
        val teamcityName : String
        if (rawMessage.contains("href")) {
            rawMessage = excludeLinkFromMessage(rawMessage)
        }
        val msgArray = rawMessage.split(" ")
        if (msgArray.size < 3) {
            println("Некорректное число аргументов : ${msgArray}")
            return null
        }
        val teamcity = teamcityRepository.findByTeamcityAddress(msgArray[1])
        if (teamcity == null) {
            println("Не найдена запись о сборочном сервере ${msgArray[1]}")
            return null
        }

        if (msgArray.size == 2) {
            return buildRepository.save(BuildPlan(msgArray[0], teamcity, emptyList()))
        } else {
            return buildRepository.save(
                    BuildPlan(msgArray[0], teamcity,
                            message.substringAfter(msgArray[1]).split(",").stream().map(String::trim).toList()))
        }
    }

    fun parseWebLogicMessage(message: String, botUser: BotUser): WebLogic? {
        var rawMessage = message.substringAfter("!add_weblogic ").trim()
        if (rawMessage.contains("href")) {
            rawMessage = excludeLinkFromMessage(rawMessage)
        }

        val msgArray = rawMessage.split(" ")
        if (msgArray.size < 3) {
            println("Некорректное число аргументов : ${msgArray}")
            return null
        } else if (msgArray.size == 3) {
            return weblogicRepository.save(WebLogic(msgArray[0], msgArray[1], msgArray[2], mutableListOf<String>()))
        } else {
            val webLogic = WebLogic(msgArray[0], msgArray[1], msgArray[2],
                    message.substringAfter(msgArray[2]).split(",").stream().map(String::trim).toList() as MutableList<String>)
            return weblogicRepository.save(webLogic)
        }
    }

    fun isStart(message: String) = message.startsWith("!start")

}
