 package net.anviprojects.builderBot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

 @SpringBootApplication
@EnableScheduling
class BuilderBotApplication

fun main(args: Array<String>) {

	//runApplication<BuilderBotApplication>(*args)
	val appContext = SpringApplication.run(BuilderBotApplication::class.java, *args)

	val startupConfiguration = appContext.getBean("startupConfiguration") as StartupConfiguration
	startupConfiguration.initConnections()
}

