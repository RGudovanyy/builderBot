package net.anviprojects.builderBot.services

import com.samczsun.skype4j.chat.Chat
import com.samczsun.skype4j.chat.messages.ChatMessage
import net.anviprojects.builderBot.model.BotUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Сервис для обработки административных сообщений, например завершить работу и т.д.
 */
@Service
class SystemMessageService {

    val supremeCommanders = listOf("boneliker")

    @Autowired
    lateinit var messageProcessor: MessageProcessor

    fun onMessage(message: ChatMessage, botUser: BotUser) {

        val rawMessage = message.content.asPlaintext().toLowerCase()

        when {
            /*messageProcessor.isAddTeamcity(rawMessage) -> addTeamcity(rawMessage, botUser, message.chat)
            messageProcessor.isAddBuildPlan(rawMessage) -> addBuildPlan(rawMessage, botUser, message.chat)
            messageProcessor.isAddWebLogic(rawMessage) -> addWebLogic(rawMessage, botUser, message.chat)
            */
            messageProcessor.isGetHelp(rawMessage) -> printHelp(message.chat)
            messageProcessor.isShutdown(rawMessage) -> shutDown(botUser, message.chat)
        }



    }

    private fun printHelp(chat: Chat) {

        chat.sendMessage("===== Помощь =====\n" +
                "!add_teamcity [teamcity_address] [teamcity_login] [teamcity_password] - добавляет для текущего " +
                "пользователя новую запись сборочного сервера\n" +
                "Пример: !add_teamcity http://someteamcityname.otr.ru teamctyuser teamcitypass\n\n" +
                "!add_buildplan [buildplan_name] [teamcity_address] [alias_1, alias_2 ... aliasN] - добавляет для текущего " +
                "пользователя билдплан, с указанием к какому сборочному серверу он относится, а так же список алиасов, по которым" +
                "можно вызвать данный билдплан\n\n" +
                "Пример: !add_buildplan UfosBuildPlan http://someteamcityname.otr.ru buildAlias, билд_план, мастер\n\n" +
                "!add_weblogic [weblogic_address] - добавляет для текущего пользователя новую запись стенда WebLogic\n" +
                "==========")
    }

    private fun shutDown(botUser: BotUser, chat: Chat) {
        if (botUser.chatUser.username in supremeCommanders) {
            chat.sendMessage("Ну, я пошел")
            System.exit(0)
        } else {
            chat.sendMessage("Ты кто такой? Давай досвидания")
        }
    }

}