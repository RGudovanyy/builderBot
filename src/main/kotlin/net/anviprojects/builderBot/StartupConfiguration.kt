package net.anviprojects.builderBot

import net.anviprojects.builderBot.services.CommonMessageService
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.SystemMessageService
import net.anviprojects.builderBot.skype.SkypeFacade
import net.anviprojects.builderBot.telegram.TelegramFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.net.Authenticator
import java.net.PasswordAuthentication

@Configuration
@PropertySource("classpath:auth.properties")
class StartupConfiguration() {

    @Value("\${botname:}")
    lateinit var SKYPE_BOT_USERNAME: String
    @Value("\${botpass:}")
    lateinit var SKYPE_BOT_PASSWORD: String
    @Value("\${proxyuser:}")
    lateinit var TG_PROXY_USERNAME: String
    @Value("\${proxypass:}")
    lateinit var TG_PROXY_PASSWORD: String
    @Value("\${proxyhost:}")
    lateinit var TG_PROXY_HOST: String
    @Value("\${proxyport:}")
    lateinit var TG_PROXY_PORT: String
    @Value("\${bottoken:}")
    lateinit var TG_BOT_TOKEN: String


    @Autowired
    lateinit var messageProcessor : MessageProcessor
    @Autowired
    lateinit var commonMessageService: CommonMessageService
    @Autowired
    lateinit var systemMessageService: SystemMessageService

    lateinit var skypeFacade: SkypeFacade
    lateinit var telegramFacade : TelegramFacade



    fun initConnections() {
        skypeFacade = SkypeFacade(messageProcessor, commonMessageService, systemMessageService)
        skypeFacade.connect(SKYPE_BOT_USERNAME, SKYPE_BOT_PASSWORD)

        if (TG_PROXY_USERNAME.isNotEmpty() && TG_PROXY_PASSWORD.isNotEmpty()) {
            Authenticator.setDefault(object : Authenticator(){
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(TG_PROXY_USERNAME, TG_PROXY_PASSWORD.toCharArray())
                }
            })
        }
        ApiContextInitializer.init()
        val botOptions = getProxyOptions()
        telegramFacade = TelegramFacade(TG_BOT_TOKEN, botOptions,  messageProcessor, commonMessageService, systemMessageService)

        val telegramBotsApi = TelegramBotsApi()
        telegramBotsApi.registerBot(telegramFacade)

    }

    private fun getProxyOptions(): DefaultBotOptions {
        val botOptions = ApiContext.getInstance(DefaultBotOptions::class.java)
        botOptions.proxyHost = TG_PROXY_HOST
        botOptions.proxyPort = TG_PROXY_PORT.toInt()
        botOptions.proxyType = DefaultBotOptions.ProxyType.SOCKS5
        return botOptions
    }
}