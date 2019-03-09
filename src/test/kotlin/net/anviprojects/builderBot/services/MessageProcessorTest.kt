package net.anviprojects.builderBot.services

import com.samczsun.skype4j.formatting.Message
import com.samczsun.skype4j.formatting.Text
import com.samczsun.skype4j.user.User
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.tasks.Task
import net.anviprojects.builderBot.tasks.TaskType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

class MessageProcessorTest {
    @Mock
    lateinit var userMock : User
    @InjectMocks
    val messageProcessor = MessageProcessor()

    lateinit var user : BotUser

    @Before
    fun setUp() {
        initMocks(this)
        user = BotUser(userMock)
    }

    @Test
    fun `make build task`(){
        val message = Message.create().with(Text.plain("Сборка мейн"))
        val resultTask = Task("main", TaskType.BUILD, user)
        val expectedTasks = messageProcessor.createTasks(message, user)
        assertEquals(resultTask, expectedTasks.get(0))
    }

    @Test
    fun `make two build tasks with and separator`() {
        val message = Message.create().with(Text.plain("Сборка мейн и стейбл"))
        val resultTasks = listOf(Task("main", TaskType.BUILD, user), Task("stable", TaskType.BUILD, user))
        val expectedTasks = messageProcessor.createTasks(message, user)
        assertTrue(expectedTasks.size == 2)
        assertEquals(resultTasks.get(0), expectedTasks.get(0))
        assertEquals(resultTasks.get(1), expectedTasks.get(1))
    }

    @Test
    fun `make two build tasks with colon separator`() {
        val message = Message.create().with(Text.plain("Сборка мейн, стейбл"))
        val resultTasks = listOf(Task("main", TaskType.BUILD, user), Task("stable", TaskType.BUILD, user))
        val expectedTasks = messageProcessor.createTasks(message, user)
        assertTrue(expectedTasks.size == 2)
        assertEquals(resultTasks.get(0), expectedTasks.get(0))
        assertEquals(resultTasks.get(1), expectedTasks.get(1))
    }

    @Test
    fun `make deploy task`() {
        val message = Message.create().with(Text.plain("Обновление мейн"))
        val resultTask = Task("mainStand", TaskType.DEPLOY, user)
        val expectedTasks = messageProcessor.createTasks(message, user)
        assertEquals(resultTask, expectedTasks.get(0))
    }

    @Test
    fun `make two deploy tasks with colon separator`() {
        val message = Message.create().with(Text.plain("Обновление мейн, стейбл"))
        val resultTasks = listOf(Task("mainStand", TaskType.DEPLOY, user), Task("stableStand", TaskType.DEPLOY, user))
        val expectedTasks = messageProcessor.createTasks(message, user)
        assertTrue(expectedTasks.size == 2)
        assertEquals(resultTasks.get(0), expectedTasks.get(0))
        assertEquals(resultTasks.get(1), expectedTasks.get(1))
    }
}

