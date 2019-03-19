package net.anviprojects.builderBot.skype.listeners

import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.chat.message.MessageEvent
import com.samczsun.skype4j.user.User
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.ConversationContext
import net.anviprojects.builderBot.model.MessageAdapter
import net.anviprojects.builderBot.services.CommonMessageService
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.SystemMessageService

class MessageListener(val username : String, val messageProcessor: MessageProcessor,
                      val commonMessageService: CommonMessageService, val systemMessageService: SystemMessageService): Listener {

    val userContexts = HashMap<User, ConversationContext>()

    @EventHandler
    fun onMessage(event : MessageEvent){

        // для того, чтоб не реагировал на свои же сообщения
        if (!isMyself(event)) {
            val message = MessageAdapter.adapt(event.message)
            if (userContexts.containsKey(event.message.sender)) {
                userContexts.get(event.message.sender)!!.addMessageToConversation(message)
            } else {
                val botUser = BotUser(event.message.sender)
                val conversationContext = ConversationContext(botUser, messageProcessor,
                        commonMessageService, systemMessageService)
                conversationContext.addMessageToConversation(message)
                userContexts.put(event.message.sender, conversationContext)
            }
        }
    }

    private fun isMyself(event: MessageEvent) = event.message.sender.username.equals(username)
}