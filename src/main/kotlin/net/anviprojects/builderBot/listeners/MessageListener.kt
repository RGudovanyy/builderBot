package net.anviprojects.builderBot.listeners

import com.samczsun.skype4j.Skype
import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.chat.message.MessageEvent
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.tasks.Task

class MessageListener(val skype : Skype, val messageProcessor: MessageProcessor) : Listener {

    @EventHandler
    fun onMessage(event : MessageEvent){

        // для того, чтоб не реагировал на свои же сообщения
        if (!isMyself(event)) {
            if (event.message.content.asPlaintext().contains("!shutdown")) {
                event.chat.sendMessage("Пока-пока")
                System.exit(0)
            }

            val botUser = BotUser.resolveUser(event.message.sender)
            val tasks = messageProcessor.createTasks(event.message.content, botUser)
            event.chat.sendMessage(echoMessage(tasks))
        }
    }

    private fun echoMessage(tasks: List<Task>): String {
        val res = StringBuilder().append("Выполняю: \n")
        tasks.stream().forEach { res.append(it) }
        return res.toString()
    }

    private fun isMyself(event: MessageEvent) = event.message.sender.username.equals(skype.username)
}