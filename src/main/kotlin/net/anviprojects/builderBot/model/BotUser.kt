package net.anviprojects.builderBot.model

/**
 * Класс представляюший собой пользователя бота.
 * Содержит в себе как юзернейм из скайпа, так и авторизационные данные для доступа к серверу CI и стендам
 */

class BotUser (username : String) {
    var id : Long = 0
    val username: String
    val teamcities = mutableListOf<Teamcity>()
    val weblogics = mutableListOf<WebLogic>()
    val builds = mutableListOf<BuildPlan>()

    lateinit var userPassTeamcity : Pair<String, String> // пока закладываемся на то, что для всех тимсити один и тот же пользователь и пароль

    init {
        this.username = username
    }

}