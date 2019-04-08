package net.anviprojects.builderBot.skype

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer
import com.microsoft.bot.connector.implementation.ConnectorClientImpl
import com.microsoft.bot.schema.models.Activity
import com.microsoft.bot.schema.models.ResourceResponse
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.ConversationContext
import net.anviprojects.builderBot.model.MessageAdapter
import net.anviprojects.builderBot.services.ServiceHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(path = arrayOf("/api/messages"))
class SkypeController(private val authenticator: SkypeAuthenticator, val serviceHolder : ServiceHolder) {

    val userContexts = HashMap<String, ConversationContext>()

    @PostMapping("")
    fun createResponses (
            @RequestHeader(name = "Authorization")
            authHeader : String,
            @RequestBody @Valid @JsonDeserialize(using = DateTimeDeserializer::class)
            activity : Activity) : List<ResourceResponse> {

        if (authHeader.isBlank()) {
            // throw ex
        }

        authenticator.setToken("token", authHeader)
        authenticator.authenticateRequest(activity)

        val connector = ConnectorClientImpl(activity.serviceUrl(), authenticator.credentials)

        val conversation = connector.conversations()

        val botUser = BotUser(activity.from().id())
        lateinit var conversationContext : ConversationContext
        if (userContexts.containsKey(botUser.username)) {
            conversationContext = userContexts.get(botUser.username)!!
        } else {
            conversationContext = ConversationContext(botUser, serviceHolder)
            userContexts.put(botUser.username, conversationContext)
        }
        val responseMessage = conversationContext.addMessageToConversationAndReply(MessageAdapter.adapt(activity))
        val response = SkypeChat(conversation, activity).sendMessage(responseMessage)

        return listOf(response)
    }
}
