package net.anviprojects.builderBot.model

import javax.persistence.*

@Entity
class BuildPlan (val name: String,
                 @ManyToOne(fetch = FetchType.EAGER)
                 var teamcity: Teamcity,
                 @ElementCollection
                 val aliases: List<String>) : AbstractModel<Long>() {

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false

        other as BuildPlan

        if (name != other.name) return false
        if (teamcity != other.teamcity) return false
        if (aliases != other.aliases) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + teamcity.hashCode()
        result = 31 * result + aliases.hashCode()
        return result
    }
}

@Entity
class Teamcity(val teamcityAddress : String) : AbstractModel<Long>() {
    @OneToMany(fetch = FetchType.EAGER)
    var buildPlans : MutableList<BuildPlan> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false

        other as Teamcity

        if (teamcityAddress != other.teamcityAddress) return false
        if (buildPlans != other.buildPlans) return false

        return true
    }

    override fun hashCode(): Int {
        var result = teamcityAddress.hashCode()
        result = 31 * result + buildPlans.hashCode()
        return result
    }
}

@Entity
class WebLogic(val weblogicAddress : String, val username : String, val password : String,
               @ElementCollection
               var aliases : MutableList<String>) : AbstractModel<Long>() {

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false

        other as WebLogic

        if (weblogicAddress != other.weblogicAddress) return false
        if (aliases != other.aliases) return false

        return true
    }

    override fun hashCode(): Int {
        var result = weblogicAddress.hashCode()
        result = 31 * result + aliases.hashCode()
        return result
    }
}


@Entity
class BotUser(val username : String) : AbstractModel<Long>() {
    lateinit var teamcityLogin : String
    lateinit var teamcityPassword : String
}


data class Message(val content : String, val sentTime : Long, val sender : String, val chat: MessengerChat)