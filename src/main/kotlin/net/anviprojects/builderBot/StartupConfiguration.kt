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
    lateinit var botUsername: String
    @Value("\${botpass:}")
    lateinit var botPassword: String

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
        skypeFacade.connect(botUsername, botPassword)
    }

}