package net.anviprojects.builderBot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BuilderBotApplication

fun main(args: Array<String>) {

	runApplication<BuilderBotApplication>(*args)
	//val appContext = SpringApplication.run(BuilderBotApplication::class.java, *args)

}

