package net.anviprojects.builderBot.skype

import com.samczsun.skype4j.chat.Chat
import net.anviprojects.builderBot.model.MessengerChat

class SkypeChat (val skypeChat : Chat) : MessengerChat {

    override fun sendMessage(chatMessage: String) {
        skypeChat.sendMessage(chatMessage)
    }
}