package net.anviprojects.builderBot.skype

import com.microsoft.bot.connector.Conversations
import com.microsoft.bot.schema.models.Activity
import com.microsoft.bot.schema.models.ActivityTypes
import com.microsoft.bot.schema.models.ResourceResponse
import com.samczsun.skype4j.chat.Chat
import net.anviprojects.builderBot.model.MessengerChat

class SkypeChat (val conversations: Conversations, val requestActivity : Activity) : MessengerChat {

    override fun sendMessage(chatMessage: String) : ResourceResponse {
        return conversations.sendToConversation(requestActivity.conversation().id(),
                Activity().withType(ActivityTypes.MESSAGE)
                        .withRecipient(requestActivity.from())
                        .withFrom(requestActivity.recipient())
                        .withText(chatMessage))
    }
}