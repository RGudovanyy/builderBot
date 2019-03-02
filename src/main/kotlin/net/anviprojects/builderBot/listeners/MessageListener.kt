package net.anviprojects.builderBot.listeners

import com.samczsun.skype4j.Skype
import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.chat.message.MessageEvent

class MessageListener(val skype : Skype) : Listener {
    @EventHandler
    fun onMessage(event : MessageEvent){

        // для того, чтоб не реагировал на свои же сообщения
        if (!isMyself(event)) {
            println("Message: ${event.message.content}")
            event.chat.sendMessage(event.message.content)
        }
    }

    private fun isMyself(event: MessageEvent) = event.message.sender.username.equals(skype.username)
}