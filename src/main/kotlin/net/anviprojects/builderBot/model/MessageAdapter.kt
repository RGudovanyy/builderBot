package net.anviprojects.builderBot.model

import com.samczsun.skype4j.chat.messages.ChatMessage
import net.anviprojects.builderBot.skype.SkypeChat

class MessageAdapter {


    companion object {
        fun adapt(chatMessage: ChatMessage) : Message {
            return Message(chatMessage.content.asPlaintext(), chatMessage.sentTime, chatMessage.sender.username, SkypeChat(chatMessage.chat))
        }
    }
}
