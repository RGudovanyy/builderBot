package net.anviprojects.builderBot.skype.listeners

import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.chat.message.MessageEvent
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.ConversationContext
import net.anviprojects.builderBot.model.MessageAdapter
import net.anviprojects.builderBot.services.ServiceHolder

class MessageListener(val username : String, val serviceHolder: ServiceHolder): Listener {

    val userContexts = HashMap<String, ConversationContext>()

    @EventHandler
    fun onMessage(event : MessageEvent){

        // для того, чтоб не реагировал на свои же сообщения
        if (!isMyself(event)) {
            val message = MessageAdapter.adapt(event.message)
            if (userContexts.containsKey(event.message.sender.username)) {
                userContexts.get(event.message.sender.username)!!.addMessageToConversation(message)
            } else {
                val botUser = BotUser(event.message.sender.username) // TODO сначала пробуем получить пользака из БД, и потом уже создаем
                val conversationContext = ConversationContext(botUser, serviceHolder)
                conversationContext.addMessageToConversation(message)
                userContexts.put(botUser.username, conversationContext)
            }
        }
    }

    private fun isMyself(event: MessageEvent) = event.message.sender.username.equals(username)
}