package net.anviprojects.builderBot.services

import com.samczsun.skype4j.user.User
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.tasks.Task
import net.anviprojects.builderBot.tasks.TaskType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class MessageProcessorTest {
    @Mock
    lateinit var userMock : User

    @Autowired
    lateinit var messageProcessor : MessageProcessor

    /*@InjectMocks
    val messageProcessor = MessageProcessor(startupConfiguration)
*/
    lateinit var user : BotUser


    @Before
    fun setUp() {
        initMocks(this)
        user = BotUser(userMock)

        /*Mockito.`when`(startupConfiguration.buildPurposesParam).thenReturn("сборка, build, собери, собрать")
        Mockito.`when`(startupConfiguration.deployPurposesParam).thenReturn("обновление, обнови, поставка")
        Mockito.`when`(startupConfiguration.rebootPurposesParam).thenReturn("ребутни, ребут, перезагрузка")*/


        messageProcessor.placeholderRepository.builds.add(BuildPlan("main", null, listOf("мейн")))
        messageProcessor.placeholderRepository.builds.add(BuildPlan("stable", null, listOf("стейбл")))
        messageProcessor.placeholderRepository.weblogics.add(WebLogic("mainStand", listOf("мейн")))
        messageProcessor.placeholderRepository.weblogics.add(WebLogic("stableStand", listOf("стейбл")))
    }

    @Test
    fun `make two build tasks with and separator`() {
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

