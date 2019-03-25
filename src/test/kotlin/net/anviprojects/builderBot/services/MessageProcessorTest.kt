package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.repositories.BuildRepository
import net.anviprojects.builderBot.repositories.WeblogicRepository
import net.anviprojects.builderBot.tasks.BuildTask
import net.anviprojects.builderBot.tasks.DeployTask
import net.anviprojects.builderBot.tasks.TaskType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MessageProcessorTest {

    val user : BotUser = BotUser("userMock")
    val teamcity : Teamcity = Teamcity("http://someaddress.otr.ru")
    val mainBuildPlan : BuildPlan = BuildPlan("main", teamcity, listOf("мейн"))
    val stableBuildPlan : BuildPlan = BuildPlan("stable", teamcity, listOf("стейбл"))
    val mainWeblogic : WebLogic = WebLogic("http://weblogicserver.otr.ru", "username", "password", mutableListOf("мейн"))
    val stableWeblogic : WebLogic = WebLogic("http://weblogicserver.otr.ru", "username", "password", mutableListOf("стейбл"))

    @Mock
    lateinit var weblogicRepositoryMock: WeblogicRepository
    @Mock
    lateinit var buildRepositoryMock: BuildRepository

    @Autowired
    @InjectMocks
    lateinit var messageProcessor : MessageProcessor


    @BeforeEach
    fun setUp() {
        initMocks(this)
        mainBuildPlan.deployAlias = "мейн"
        stableBuildPlan.deployAlias = "стейбл"
        messageProcessor.weblogicRepository = weblogicRepositoryMock
        messageProcessor.buildRepository = buildRepositoryMock

        `when`(weblogicRepositoryMock.findByAlias("мейн")).thenReturn(mainWeblogic)
        `when`(weblogicRepositoryMock.findByAlias("стейбл")).thenReturn(stableWeblogic)
        `when`(buildRepositoryMock.findByAlias("мейн")).thenReturn(mainBuildPlan)
        `when`(buildRepositoryMock.findByAlias("стейбл")).thenReturn(stableBuildPlan)
        `when`(buildRepositoryMock.findAll()).thenReturn(mutableListOf(mainBuildPlan, stableBuildPlan))
        `when`(buildRepositoryMock.findAllDeployBuildPlans()).thenReturn(listOf(mainBuildPlan, stableBuildPlan))
    }

    @Test
    fun `make two build tasks with 'and' separator`() {
        val resultTasks = listOf(BuildTask(mainBuildPlan, TaskType.BUILD, user), BuildTask(stableBuildPlan, TaskType.BUILD, user))
        val expectedTasks = messageProcessor.createTasks("Сборка мейн и стейбл", user, TaskType.BUILD)
        assertTrue(expectedTasks.size == 2)
        assert(expectedTasks.contains(resultTasks.get(0)))
        assert(expectedTasks.contains(resultTasks.get(1)))
    }

    @Test
    fun `make build task`(){
        val resultTask = BuildTask(mainBuildPlan, TaskType.BUILD, user)
        val expectedTasks = messageProcessor.createTasks("Сборка мейн", user, TaskType.BUILD)
        assert(expectedTasks.contains(resultTask))
    }

    @Test
    fun `make two build tasks with colon separator`() {
        val resultTasks = listOf(BuildTask(mainBuildPlan, TaskType.BUILD, user), BuildTask(stableBuildPlan, TaskType.BUILD, user))
        val expectedTasks = messageProcessor.createTasks("Сборка мейн, стейбл", user, TaskType.BUILD)
        assertTrue(expectedTasks.size == 2)
        assert(expectedTasks.contains(resultTasks.get(0)))
        assert(expectedTasks.contains(resultTasks.get(1)))
    }

    @Test
    fun `make deploy task`() {
        val resultTask = DeployTask(mainBuildPlan, mainWeblogic, TaskType.DEPLOY, user)
        val expectedTasks = messageProcessor.createTasks("Обновление мейн", user, TaskType.DEPLOY)
        assert(expectedTasks.contains(resultTask))
    }

    @Test
    fun `make two deploy tasks with colon separator`() {
        val resultTasks = listOf(DeployTask(mainBuildPlan, mainWeblogic, TaskType.DEPLOY, user),
                DeployTask(stableBuildPlan, stableWeblogic, TaskType.DEPLOY, user))
        val expectedTasks = messageProcessor.createTasks("Обновление мейн, стейбл", user, TaskType.DEPLOY)
        assertTrue(expectedTasks.size == 2)
        assert(expectedTasks.contains(resultTasks.get(0)))
        assert(expectedTasks.contains(resultTasks.get(1)))
    }
}

