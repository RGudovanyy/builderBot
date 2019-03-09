package net.anviprojects.builderBot.model

import com.samczsun.skype4j.Skype
import net.anviprojects.builderBot.StartupConfiguration
import net.anviprojects.builderBot.helper.LiveLoginHelper
import net.anviprojects.builderBot.helper.MSFTSkypeClient
import net.anviprojects.builderBot.listeners.ContactRequestListener
import net.anviprojects.builderBot.listeners.MessageListener
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.MessageService
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class SkypeFacade (startupConfiguration: StartupConfiguration, messageProcessor : MessageProcessor, messageService: MessageService) {

    final val skype: Skype = getSkype(startupConfiguration.botUsername, startupConfiguration.botPassword)

    init {
       // skype = getSkype(startupConfiguration.botUsername, startupConfiguration.botPassword)
        skype.login()
        println("Logged in")

        skype.eventDispatcher.registerListener(ContactRequestListener())
        skype.eventDispatcher.registerListener(MessageListener(skype.username, messageProcessor, messageService))

        skype.subscribe()
        println("Subscribed")
    }


    private fun getSkype(s: String, s1: String): Skype {
        val jsonObject = LiveLoginHelper.getXTokenObject(s, s1)
        val skypeToken = jsonObject.getString("skypetoken")
        val skypeId = jsonObject.getString("skypeid")
        return MSFTSkypeClient.Builder(skypeToken, skypeId).withLogger(Logger.getGlobal()).withAllResources().build()
    }

}