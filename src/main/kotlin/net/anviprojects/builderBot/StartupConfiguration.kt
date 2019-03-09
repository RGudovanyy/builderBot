package net.anviprojects.builderBot

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:auth.properties")
class StartupConfiguration() {

    @Value("\${botname:}")
    lateinit var botUsername: String
    @Value("\${botpass:}")
    lateinit var botPassword: String

}