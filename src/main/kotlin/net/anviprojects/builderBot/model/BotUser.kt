package net.anviprojects.builderBot.model

import com.samczsun.skype4j.user.User

/**
 * Класс представляюший собой пользователя бота.
 * Содержит в себе как юзернейм из скайпа, так и авторизационные данные для доступа к серверу CI и стендам
 */
class BotUser (private val chatUser: User) {

    fun notifyClearContext() {
        chatUser.chat.sendMessage("Время ожидания ответа истекло")

    }

    var delayTimeProperty: Long = 20

}