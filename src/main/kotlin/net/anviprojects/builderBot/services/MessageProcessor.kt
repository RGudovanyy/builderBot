package net.anviprojects.builderBot.services

import com.samczsun.skype4j.formatting.Message
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.tasks.Task
import net.anviprojects.builderBot.tasks.TaskType
import org.springframework.stereotype.Component


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
    val buildToAliases = mapOf(Pair("main", listOf("main", "мейн")),
                                Pair("stable", listOf("stable", "стейбл")))
    val serverToAliases = mapOf(Pair("mainStand", listOf("main", "мейн")),
            Pair("stableStand", listOf("stable", "стейбл")))
    val buildPurposes = listOf("сборк", "собер", "собра")
    val deployPurposes = listOf("обнов", "постав")
    val rebootPurposes = listOf("ребут", "перезагр")

    fun createTasks(message: Message, user: BotUser): List<Task> {
        val tasks = mutableListOf<Task>()
        val msgContent = message.asPlaintext().toLowerCase()
        // вариант так себе - лишний раз проходим по ненужным спискам
        _createTasks(msgContent, tasks, user, buildPurposes, buildToAliases, TaskType.BUILD)
        _createTasks(msgContent, tasks, user, deployPurposes, serverToAliases, TaskType.DEPLOY)
        _createTasks(msgContent, tasks, user, rebootPurposes, serverToAliases, TaskType.REBOOT)
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

    fun isYes(message: Message) = message.asPlaintext().toLowerCase().equals("да") || message.asPlaintext().equals("+")

    fun isNo(message: Message) = message.asPlaintext().toLowerCase().equals("нет") || message.asPlaintext().equals("-")

    fun isTaskMessage(content: Message) = buildPurposes.stream().anyMatch { content.asPlaintext().toLowerCase().contains(it) }
            || deployPurposes.stream().anyMatch { content.asPlaintext().toLowerCase().contains(it) }
            || rebootPurposes.stream().anyMatch { content.asPlaintext().toLowerCase().contains(it) }

    fun isSystemMessage(content: Message) = content.asPlaintext().startsWith("!")


}
