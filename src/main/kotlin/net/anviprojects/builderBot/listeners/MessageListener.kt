package net.anviprojects.builderBot.listeners

import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.chat.message.MessageEvent
import com.samczsun.skype4j.user.User
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.ConversationContext
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.MessageService

class MessageListener(val username : String, val messageProcessor: MessageProcessor, val messageService: MessageService): Listener {

    val userContexts = HashMap<User, ConversationContext>()

    @EventHandler
    fun onMessage(event : MessageEvent){

        // для того, чтоб не реагировал на свои же сообщения
        if (!isMyself(event)) {
            if (userContexts.containsKey(event.message.sender)) {
                userContexts.get(event.message.sender)!!.addMessageToConversation(event.message)
            } else {
                val botUser = BotUser(event.message.sender)
                val conversationContext = ConversationContext(event.message.sentTime, botUser, messageProcessor, messageService)
                conversationContext.addMessageToConversation(event.message)
                userContexts.put(event.message.sender, conversationContext)
            }
        }
    }

    private fun isMyself(event: MessageEvent) = event.message.sender.username.equals(username)
}