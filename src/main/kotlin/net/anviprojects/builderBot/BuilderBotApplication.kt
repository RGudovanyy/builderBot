package net.anviprojects.builderBot

import com.samczsun.skype4j.Skype
import net.anviprojects.builderBot.helper.LiveLoginHelper
import net.anviprojects.builderBot.helper.MSFTSkypeClient
import net.anviprojects.builderBot.listeners.ContactRequestListener
import net.anviprojects.builderBot.listeners.MessageListener
import net.anviprojects.builderBot.services.MessageProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import java.util.logging.Logger

@SpringBootApplication
@PropertySource("classpath:auth.properties")
class BuilderBotApplication {

	//TODO работу с пропертями нужно вынести в отдельный класс конфигурации
	@Value("\${botname:}")
	lateinit var botUsername: String
	@Value("\${botpass:}")
	lateinit var botPassword: String
}

fun main(args: Array<String>) {

	//runApplication<BuilderBotApplication>(*args)
	val appContext = SpringApplication.run(BuilderBotApplication::class.java, *args)
	val botApplication = appContext.getBean(BuilderBotApplication::class.java)

	val skype = getSkype(botApplication.botUsername, botApplication.botPassword)
	skype.login()
	println("Logged in")

	skype.eventDispatcher.registerListener(ContactRequestListener())
	skype.eventDispatcher.registerListener(MessageListener(skype, appContext.getBean(MessageProcessor::class.java)))

	skype.subscribe()
	println("Subscribed")

	//Thread.sleep(1000)
	//skype.logout()


}

private fun getSkype(s: String, s1: String): Skype {
	val jsonObject = LiveLoginHelper.getXTokenObject(s, s1)
	val skypeToken = jsonObject.getString("skypetoken")
	val skypeId = jsonObject.getString("skypeid")
	return MSFTSkypeClient.Builder(skypeToken, skypeId).withLogger(Logger.getGlobal()).withAllResources().build()
}

