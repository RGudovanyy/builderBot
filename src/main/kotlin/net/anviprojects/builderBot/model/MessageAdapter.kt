package net.anviprojects.builderBot.model

import com.microsoft.bot.schema.models.Activity
import com.samczsun.skype4j.chat.messages.ChatMessage
import net.anviprojects.builderBot.skype.SkypeChat

class MessageAdapter {


    companion object {
        fun adapt(activity: Activity) : Message {
            return Message(activity.text(), activity.timestamp().millis, activity.from().id())
        }

        fun adapt(telegramMessage : org.telegram.telegrambots.meta.api.objects.Message) : Message {
            return Message(telegramMessage.text.replaceFirst("/", "!"), telegramMessage.date.toLong(), telegramMessage.from.userName)
        }


     }
}
