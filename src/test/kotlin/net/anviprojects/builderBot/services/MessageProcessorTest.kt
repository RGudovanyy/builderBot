package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.tasks.Task
import net.anviprojects.builderBot.tasks.TaskType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MessageProcessorTest(@Autowired val messageProcessor : MessageProcessor) {

    lateinit var user : BotUser
    lateinit var teamcity : Teamcity

    @BeforeAll
    fun setUp() {
        user = BotUser("userMock")
        teamcity = Teamcity("http://someaddress.otr.ru")

        messageProcessor.placeholderRepository.builds.add(BuildPlan("main", teamcity, listOf("мейн")))
        messageProcessor.placeholderRepository.builds.add(BuildPlan("stable", teamcity, listOf("стейбл")))
        messageProcessor.placeholderRepository.weblogics.add(WebLogic("mainStand", "username", "password", mutableListOf("мейн")))
        messageProcessor.placeholderRepository.weblogics.add(WebLogic("stableStand", "username", "password", mutableListOf("стейбл")))
    }

    @Test
    fun `make two build tasks with 'and' separator`() {
        val resultTasks = listOf(Task("main", TaskType.BUILD, user), Task("stable", TaskType.BUILD, user))
        val expectedTasks = messageProcessor.createTasks("Сборка мейн и стейбл", user)
        assertTrue(expectedTasks.size == 2)
        assertEquals(resultTasks.get(0), expectedTasks.get(0))
        assertEquals(resultTasks.get(1), expectedTasks.get(1))
    }

    @Test
    fun `make build task`(){
        val resultTask = Task("main", TaskType.BUILD, user)
        val expectedTasks = messageProcessor.createTasks("Сборка мейн", user)
        assertEquals(resultTask, expectedTasks.get(0))
    }

    @Test
    fun `make two build tasks with colon separator`() {
        val resultTasks = listOf(Task("main", TaskType.BUILD, user), Task("stable", TaskType.BUILD, user))
        val expectedTasks = messageProcessor.createTasks("Сборка мейн, стейбл", user)
        assertTrue(expectedTasks.size == 2)
        assertEquals(resultTasks.get(0), expectedTasks.get(0))
        assertEquals(resultTasks.get(1), expectedTasks.get(1))
    }

    @Test
    fun `make deploy task`() {
        val resultTask = Task("mainStand", TaskType.DEPLOY, user)
        val expectedTasks = messageProcessor.createTasks("Обновление мейн", user)
        assertEquals(resultTask, expectedTasks.get(0))
    }

    @Test
    fun `make two deploy tasks with colon separator`() {
        val resultTasks = listOf(Task("mainStand", TaskType.DEPLOY, user), Task("stableStand", TaskType.DEPLOY, user))
        val expectedTasks = messageProcessor.createTasks("Обновление мейн, стейбл", user)
        assertTrue(expectedTasks.size == 2)
        assertEquals(resultTasks.get(0), expectedTasks.get(0))
        assertEquals(resultTasks.get(1), expectedTasks.get(1))
    }
}

