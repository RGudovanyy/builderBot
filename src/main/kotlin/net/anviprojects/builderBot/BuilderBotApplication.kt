package net.anviprojects.builderBot

import com.samczsun.skype4j.Skype
import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.chat.message.MessageEvent
import com.samczsun.skype4j.events.contact.ContactRequestEvent
import net.anviprojects.builderBot.helper.LiveLoginHelper
import net.anviprojects.builderBot.helper.MSFTSkypeClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import java.util.logging.Logger

@SpringBootApplication
@PropertySource("classpath:auth.properties")
class BuilderBotApplication {

	/*TODO
1. вынести листенер в отдельный класс. Возможно даже разделить на несколько по типам событий - MessageEventListener,
ContactRequestListener и т.д.
2. сообщения передавать в MessageProcessor. На первом этапе сделать в нем мапу, и пачку подготовленных ответов, которые
будем пересылать отправителю
 */
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

	skype.eventDispatcher.registerListener(object: Listener {
		@EventHandler
		fun onMessage(e : MessageEvent){

			// для того, чтоб не реагировал на свои же сообщения
			if (!e.message.sender.username.equals(botApplication.botUsername)) {
				println("Message: ${e.message.content}")
				e.chat.sendMessage(e.message.content)
			}
		}

		@EventHandler
		fun onContact(e : ContactRequestEvent) {
			e.request.sender.authorize()
		}
	})

	skype.subscribe()
	println("Subscribed")

	//Thread.sleep(1000)
	//skype.logout()


}

fun getSkype(s: String, s1: String): Skype {
	val jsonObject = LiveLoginHelper.getXTokenObject(s, s1)
	val skypeToken = jsonObject.getString("skypetoken")
	val skypeId = jsonObject.getString("skypeid")
	return MSFTSkypeClient.Builder(skypeToken, skypeId).withLogger(Logger.getGlobal()).withAllResources().build()
}

