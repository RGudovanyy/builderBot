package net.anviprojects.builderBot

import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import net.anviprojects.builderBot.repositories.BotUserRepository
import net.anviprojects.builderBot.repositories.BuildRepository
import net.anviprojects.builderBot.repositories.TeamcityRepository
import net.anviprojects.builderBot.repositories.WeblogicRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class RepositoryTests (@Autowired val teamcityRepository : TeamcityRepository,
                       @Autowired val buildRepository : BuildRepository,
                       @Autowired val weblogicRepository: WeblogicRepository,
                       @Autowired val botUserRepository: BotUserRepository) {

    @AfterEach
    fun refresh(){
        buildRepository.deleteAll()
        teamcityRepository.deleteAll()
        weblogicRepository.deleteAll()
        botUserRepository.deleteAll()
    }

    @Test
    fun `teamcity persistance test`() {
        val t = Teamcity("http://someteamcityname.otr.ru")
        teamcityRepository.save(t)
        assert(teamcityRepository.findAll().size == 1)
    }

    @Test
    fun `teamcity get by id test`() {
        teamcityRepository.save(Teamcity("http://someteamcityname.otr.ru"))
        assertNotNull(teamcityRepository.findById(1))
    }

    @Test
    fun `teamcity get by address test`() {
        teamcityRepository.save(Teamcity("http://someteamcityname.otr.ru"))
        assertNotNull(teamcityRepository.findByTeamcityAddress("http://someteamcityname.otr.ru"))
    }

    @Test
    fun `buildplan persistance test`() {
        val teamcity = teamcityRepository.save(Teamcity("http://someteamcityname.otr.ru"))
        val aliases = listOf("main", "мейн")
        val buildPlan = BuildPlan("main", teamcity, aliases)
        buildRepository.save(buildPlan)

        assert(buildRepository.findAll().size == 1)
    }

    @Test
    fun `buildplan get by name test`() {
        val teamcity = teamcityRepository.save(Teamcity("http://someteamcityname.otr.ru"))
        buildRepository.save(BuildPlan("main", teamcity, listOf("main", "мейн")))

        assertNotNull(buildRepository.findByName("main"))
    }

    @Test
    fun `buildplan get by alias test`() {
        val teamcity = teamcityRepository.save(Teamcity("http://someteamcityname.otr.ru"))
        buildRepository.save(BuildPlan("main", teamcity, listOf("main", "мейн")))

        assertNotNull(buildRepository.findByAlias("мейн"))
    }

    @Test
    fun `buildplan get all by teamcity test`() {
        val teamcity = teamcityRepository.save(Teamcity("http://someteamcityname.otr.ru"))
        buildRepository.save(BuildPlan("main", teamcity, listOf("main", "мейн")))
        buildRepository.save(BuildPlan("stable", teamcity, listOf("stable", "стейбл")))

        val result = buildRepository.findAllByTeamcity_TeamcityAddress(teamcity.teamcityAddress)
        assert(result.size == 2)
    }

    @Test
    fun `weblogic persistence test`() {
        val weblogic = WebLogic("http://weblogic1.otr.ru", "username", "hidden", mutableListOf("stand"));
        weblogicRepository.save(weblogic)

        assert(weblogicRepository.findAll().size == 1)
    }

    @Test
    fun `weblogic find by weblogic adderss test`() {
        weblogicRepository.save(WebLogic("http://weblogic1.otr.ru", "username", "hidden", mutableListOf("stand")))

        assertNotNull(weblogicRepository.findByWeblogicAddress("http://weblogic1.otr.ru"))
    }

    @Test
    fun `weblogic find by alias test`() {
        weblogicRepository.save(WebLogic("http://weblogic1.otr.ru", "username", "hidden", mutableListOf("stand")))

        assertNotNull(weblogicRepository.findByAlias("stand"))
    }
}