package net.anviprojects.builderBot.model

import com.samczsun.skype4j.Skype
import net.anviprojects.builderBot.helper.LiveLoginHelper
import net.anviprojects.builderBot.helper.MSFTSkypeClient
import net.anviprojects.builderBot.listeners.ContactRequestListener
import net.anviprojects.builderBot.listeners.MessageListener
import net.anviprojects.builderBot.services.CommonMessageService
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.SystemMessageService
import java.util.logging.Logger

class SkypeFacade (val messageProcessor : MessageProcessor,
                   val commonMessageService: CommonMessageService, val systemMessageService: SystemMessageService) {

    lateinit var skype : Skype

    fun connect(username : String, password : String) {
        try{
            skype = getSkype(username, password)
            skype.login()
            println("Logged in")

            skype.eventDispatcher.registerListener(ContactRequestListener())
            skype.eventDispatcher.registerListener(MessageListener(skype.username, messageProcessor, commonMessageService, systemMessageService))

            skype.subscribe()
            println("Subscribed")
        } catch (e : Throwable) {
            println("Cannot connect to skype API")
        }

    }


    private fun getSkype(s: String, s1: String): Skype {
        val jsonObject = LiveLoginHelper.getXTokenObject(s, s1)
        val skypeToken = jsonObject.getString("skypetoken")
        val skypeId = jsonObject.getString("skypeid")
        return MSFTSkypeClient.Builder(skypeToken, skypeId).withLogger(Logger.getGlobal()).withAllResources().build()
    }

}