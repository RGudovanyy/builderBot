package net.anviprojects.builderBot.services

import com.samczsun.skype4j.chat.messages.ChatMessage

/**
 * Сервис для обработки административных сообщений, например завершить работу и т.д.
 */
class SystemMessageService {

    fun onMessage(message: ChatMessage) {

        if (message.content.asPlaintext().toLowerCase().equals("shutdown")) {
            System.exit(0)
        }

    }

}