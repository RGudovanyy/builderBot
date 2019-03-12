package net.anviprojects.builderBot.services

import com.samczsun.skype4j.chat.Chat
import net.anviprojects.builderBot.tasks.Task
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/*
 * Сервис для формирования задачи (Task) или списка задач (задачи выстраиваются в строгий порядок, ожидается ответ после
 * выполнения всех) и передачи ее в очередь RabbitMQ.
 * За парсинг сообщения в задачу отвечает MessageProcessor. Цепь событий:
 * MessageListener -(Message)-> MessageProcessor -(Task)-> CommonMessageService -(Task)-> AmqpTemplate
 */

@Service
class CommonMessageService {

    @Autowired
    lateinit var amqpTemplate : AmqpTemplate


    fun sendNotUnderstandMessage(chat: Chat) {
        chat.sendMessage("Не удалось распознать запрос")
    }

    fun askForSubmit(chat: Chat, tasks: ArrayList<Task>){
        if (tasks.isEmpty()) {
            chat.sendMessage("Не найдено подходящих задач")
            return
        }

        val res = StringBuilder().append("Выполняю:")
        tasks.stream().forEach { res.append("\n").append(it) }
        res.append("\nВсе верно?")
        chat.sendMessage(res.toString())
    }

    fun notifyClearContext(chat: Chat) {
        chat.sendMessage("Все добавленные задачи отменены")
    }




}