package net.anviprojects.builderBot.repositories

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TeamcityRepository : JpaRepository<Teamcity, Long>{
    fun findByTeamcityAddress(teamcityAddress : String) : Teamcity?

    fun findByBuildPlans_Name(buildPlanName: String) : Teamcity?
}

interface BuildRepository : JpaRepository<BuildPlan, Long> {
    @Query("select bp from BuildPlan bp where :alias in elements(bp.aliases)")
    fun findByAlias(alias : String) : BuildPlan?

    fun findByName(name: String) : BuildPlan?

    fun findAllByTeamcity_TeamcityAddress(teamcityAddress: String) : List<BuildPlan?>
}

interface WeblogicRepository : JpaRepository<WebLogic, Long> {

    fun findByWeblogicAddress(weblogicAddress : String) : WebLogic?

    @Query("select w from WebLogic w where :alias in elements(w.aliases)")
    fun findByAlias(alias : String) : WebLogic?
}

interface BotUserRepository : JpaRepository<BotUser, Long>
