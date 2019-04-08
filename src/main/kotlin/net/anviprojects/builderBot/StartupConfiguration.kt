package net.anviprojects.builderBot

import net.anviprojects.builderBot.services.ServiceHolder
import net.anviprojects.builderBot.skype.SkypeAuthenticator
import net.anviprojects.builderBot.telegram.TelegramFacade
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
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
class StartupConfiguration(val serviceHolder: ServiceHolder) {

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

    lateinit var telegramFacade : TelegramFacade

    @Bean(name = arrayOf("authenticator"))
    fun getAuthenticator() = SkypeAuthenticator(SKYPE_BOT_USERNAME, SKYPE_BOT_PASSWORD)

    fun initConnections() {
        if (TG_PROXY_USERNAME.isNotEmpty() && TG_PROXY_PASSWORD.isNotEmpty()) {
            Authenticator.setDefault(object : Authenticator(){
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(TG_PROXY_USERNAME, TG_PROXY_PASSWORD.toCharArray())
                }
            })
        }
        System.setProperty("java.net.useSystemProxies", "false");
        ApiContextInitializer.init()
        val botOptions = getProxyOptions()
        println("Try to connect using proxy $TG_PROXY_HOST:$TG_PROXY_PORT")
        telegramFacade = TelegramFacade(TG_BOT_TOKEN, botOptions, serviceHolder)

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