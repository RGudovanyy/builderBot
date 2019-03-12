package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.tasks.Task
import net.anviprojects.builderBot.tasks.TaskType
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
    //TODO планируется получать алиасы целей из БД - нужна возможность пополнять их список пользователем
    val buildToAliases = mutableMapOf(Pair("main", mutableListOf("main", "мейн")),
                                Pair("stable", mutableListOf("stable", "стейбл")))
    val serverToAliases = mutableMapOf(Pair("mainStand", mutableListOf("main", "мейн")),
            Pair("stableStand", mutableListOf("stable", "стейбл")))
    val buildPurposes = mutableListOf("сборк", "собер", "собра")
    val deployPurposes = mutableListOf("обнов", "постав")
    val rebootPurposes = mutableListOf("ребут", "перезагр")
    val teamcities = mutableListOf<Teamcity>()
    val weblogics = mutableListOf<WebLogic>()


    fun createTasks(message: String, user: BotUser): List<Task> {
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
            if (msgContent.contains(buildP)) {
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
    fun isAddTeamcity(message: String): Boolean = message.startsWith("!add_teamcity")
    fun isAddBuildPlan(message: String): Boolean = message.startsWith("!add_buildplan")
    fun isAddWebLogic(message: String): Boolean = message.startsWith("!add_weblogic")

    fun parseTeamcityMessage(message: String, botUser: BotUser): Teamcity? {
        // TODO сейчас botUser не используется. Нужно делать на него ссылку здесь и далее
        val msgArray = message.substringAfter("!add_teamcity ").split(" ")
        if (msgArray.size != 3) {
            return null // TODO возможно стоит заменить на выброс исключения и тогда возвращаемое значение будет null-safety
        }
        return Teamcity(msgArray[0], msgArray[1], msgArray[2])
    }

    fun parseBuildPlanMessage(message: String, botUser: BotUser): BuildPlan? {
        val msgArray = message.substringAfter("!add_buildplan ").split(" ")
        if (msgArray.size < 2) {
            return null
        } else if (msgArray.size == 2) {
            return BuildPlan(msgArray[0], null, emptyList())
        } else {
            // TODO получать тимсити из БД по имени
            return BuildPlan(msgArray[0], null, message.substringAfter(msgArray[1]).split(",").stream().map(String::trim).toList())
        }
    }

    fun parseWebLogicMessage(message: String, botUser: BotUser): WebLogic? {
        val msgArray = message.substringAfter("!add_weblogic ").trim().split(" ")
        if (msgArray.size < 1) {
            return null
        } else if (msgArray.size == 1) {
            return WebLogic(msgArray[0], emptyList())
        } else {
            return WebLogic(msgArray[0], message.substringAfter(msgArray[0]).split(",").stream().map(String::trim).toList())
        }
    }

}
