package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.MessengerChat
import net.anviprojects.builderBot.tasks.AbstractTask
import org.springframework.stereotype.Service


@Service
class CommonMessageService {

    fun sendNotUnderstandMessage(chat: MessengerChat) {
        chat.sendMessage("Не удалось распознать запрос")
    }

    fun askForSubmit(chat: MessengerChat, abstractTasks: ArrayList<AbstractTask>){
        if (abstractTasks.isEmpty()) {
            chat.sendMessage("Не найдено подходящих задач")
            return
        }

        val res = StringBuilder().append("Выполняю:")
        abstractTasks.stream().forEach { res.append("\n").append(it) }
        res.append("\nВсе верно?")
        chat.sendMessage(res.toString())
    }

    fun notifyClearContext(chat: MessengerChat) {
        chat.sendMessage("Все добавленные задачи отменены")
    }
}