package net.anviprojects.builderBot.model

import com.samczsun.skype4j.chat.messages.ChatMessage
import net.anviprojects.builderBot.skype.SkypeChat

class MessageAdapter {


    companion object {
        fun adapt(skypeMessage: ChatMessage) : Message {
            return Message(skypeMessage.content.asPlaintext(), skypeMessage.sentTime, skypeMessage.sender.username, SkypeChat(skypeMessage.chat))
        }

        fun adapt(telegramMessage : org.telegram.telegrambots.meta.api.objects.Message, chat: MessengerChat) : Message {
            return Message(telegramMessage.text.replaceFirst("/", "!"), telegramMessage.date.toLong(), telegramMessage.from.userName, chat)
        }
     }
}
