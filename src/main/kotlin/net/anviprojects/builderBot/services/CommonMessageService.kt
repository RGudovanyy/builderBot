package net.anviprojects.builderBot.services

import com.microsoft.bot.schema.models.ResourceResponse
import net.anviprojects.builderBot.model.MessengerChat
import net.anviprojects.builderBot.tasks.AbstractTask
import org.springframework.stereotype.Service


@Service
class CommonMessageService {

    fun sendNotUnderstandMessage() = "Не удалось распознать запрос"

    fun askForSubmit(abstractTasks: ArrayList<AbstractTask>) : String {
        if (abstractTasks.isEmpty()) {
            return "Не найдено подходящих задач"
        }

        val res = StringBuilder().append("Выполняю:")
        abstractTasks.forEach { res.append("\n").append(it) }
        res.append("\nВсе верно?")
        return res.toString()
    }

    fun notifyClearContext() = "Все добавленные задачи отменены"
}