package net.anviprojects.builderBot.repositories

import net.anviprojects.builderBot.model.BuildPlan
import net.anviprojects.builderBot.model.Teamcity
import net.anviprojects.builderBot.model.WebLogic
import org.springframework.stereotype.Repository

@Repository
class PlaceholderRepository {

    val teamcities = mutableListOf<Teamcity>()
    val weblogics = mutableListOf<WebLogic>()
    val builds = mutableListOf<BuildPlan>()

    fun saveOrUpdateBuild(buildPlan: BuildPlan) : BuildPlan {
        val buildOpt = builds.stream().filter { it.name.equals(buildPlan.name) }.findFirst()
        if (buildOpt.isPresent) {
            builds.add(builds.indexOf(buildOpt.get()), buildPlan)
        } else {
            builds.add(buildPlan)
        }
        return buildPlan
    }

    fun saveOrUpdateTeamcity(teamcity: Teamcity) : Teamcity {
        val teamcityOpt = teamcities.stream().filter { it.teamcityAddress.equals(teamcity.teamcityAddress) }.findFirst()
        if (teamcityOpt.isPresent) {
            teamcities.add(teamcities.indexOf(teamcityOpt.get()), teamcity)
        } else {
            teamcities.add(teamcity)
        }
        return teamcity
    }

    fun saveOrUpdateWeblogic(webLogic: WebLogic) : WebLogic {
        val weblogicOpt = weblogics.stream().filter { it.weblogicAddress.equals(webLogic.weblogicAddress) }.findFirst()
        if (weblogicOpt.isPresent) {
            weblogics.add(weblogics.indexOf(weblogicOpt.get()), webLogic)
        } else {
            weblogics.add(webLogic)
        }
        return webLogic
    }







}