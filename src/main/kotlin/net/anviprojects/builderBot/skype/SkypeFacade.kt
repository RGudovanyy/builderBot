package net.anviprojects.builderBot.skype

import com.samczsun.skype4j.Skype
import net.anviprojects.builderBot.services.ServiceHolder
import net.anviprojects.builderBot.skype.helper.LiveLoginHelper
import net.anviprojects.builderBot.skype.helper.MSFTSkypeClient
import net.anviprojects.builderBot.skype.listeners.ContactRequestListener
import net.anviprojects.builderBot.skype.listeners.MessageListener
import java.util.logging.Logger

class SkypeFacade (val serviceHolder: ServiceHolder) {

    lateinit var skype : Skype

    fun connect(username : String, password : String) {
        try{
            skype = getSkype(username, password)
            skype.login()
            println("Logged in")

            skype.eventDispatcher.registerListener(ContactRequestListener())
            skype.eventDispatcher.registerListener(MessageListener(skype.username, serviceHolder))

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