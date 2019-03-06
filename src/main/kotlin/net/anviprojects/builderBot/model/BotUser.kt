package net.anviprojects.builderBot.model

import com.samczsun.skype4j.user.User

/**
 * Класс представляюший собой пользователя бота.
 * Содержит в себе как юзернейм из скайпа, так и авторизационные данные для доступа к серверу CI и стендам
 */
class BotUser {

    companion object {
        fun resolveUser(chatUser : User) : BotUser {
            return BotUser()
        }
    }
}