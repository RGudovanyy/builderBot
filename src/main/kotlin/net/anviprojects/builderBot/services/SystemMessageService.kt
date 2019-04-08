package net.anviprojects.builderBot.services

import com.microsoft.bot.schema.models.ResourceResponse
import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.Message
import net.anviprojects.builderBot.model.MessengerChat
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

    fun onMessage(message: Message, botUser: BotUser) : String {

        val rawMessage = message.content

        when {
            messageProcessor.isAddTeamcity(rawMessage) -> return addTeamcity(rawMessage, botUser)
            messageProcessor.isAddBuildPlan(rawMessage) -> return addBuildPlan(rawMessage, botUser)
            messageProcessor.isAddWebLogic(rawMessage) -> return addWebLogic(rawMessage, botUser)

            messageProcessor.isGetHelp(rawMessage) -> return printHelp()
            messageProcessor.isShutdown(rawMessage) -> return shutDown(botUser)
            messageProcessor.isStart(rawMessage) -> return printStart()
        }
        return ""
    }

    private fun printStart(): String {
        return "Привет!"
    }

    private fun addWebLogic(message: String, botUser: BotUser): String {
        val weblogic = messageProcessor.parseWebLogicMessage(message, botUser)
        if (null == weblogic) {
            return "Не удалось добавить или обновить запись о стенде - проверьте передаваемые данные"

        }
        return "Добавлена новая запись стенда:\n$weblogic"
    }

    private fun addBuildPlan(message: String, botUser: BotUser): String {
        val buildPlan = messageProcessor.parseBuildPlanMessage(message, botUser)
        if (null == buildPlan) {
            return "Не удалось добавить или обновить запись о билд плане - проверьте передаваемые данные"
        }
        return "Добавлен новый билд план:\n$buildPlan"
    }

    private fun addTeamcity(message: String, botUser: BotUser): String {
        val teamcity = messageProcessor.parseTeamcityMessage(message, botUser)
        if (null == teamcity) { //!add_teamcity <a href="http://someteamcityname.otr.ru">http://someteamcityname.otr.ru</a> teamctyuser teamcitypass
            return "Не удалось добавить или обновить запись о сборочном сервере - проверьте передаваемые данные"
        }
        return "Добавлена новая запись о сборочном сервере:\n$teamcity"
    }

    private fun printHelp(): String {

        return "===== Помощь =====\n" +
                "!add_teamcity [teamcity_address] [teamcity_login] [teamcity_password] - добавляет для текущего " +
                "пользователя новую запись сборочного сервера\n" +
                "Пример: !add_teamcity http://someteamcityname.otr.ru teamctyuser teamcitypass\n\n" +
                "!add_buildplan [buildplan_name] [teamcity_address] [alias_1, alias_2 ... alias_N] - добавляет для текущего " +
                "пользователя билдплан, с указанием к какому сборочному серверу он относится, а так же список алиасов, по которым " +
                "можно вызвать данный билдплан\n" +
                "Пример: !add_buildplan UfosBuildPlan http://someteamcityname.otr.ru buildAlias, билд_план, мастер\n\n" +
                "!add_weblogic [weblogic_address] [alias_1, alias_2 ... alias_N] - добавляет для текущего пользователя новую " +
                "запись стенда WebLogic, а так же список алиасов которые будут на него ссылаться\n" +
                "Пример: !add_weblogic http://someweblogicname.otr.ru, стенд, stand, cntyl, ыефтв\n" +
                "=========="
    }

    private fun shutDown(botUser: BotUser): String {
        if (botUser.username in supremeCommanders) {
            System.exit(0)
            return "Ну, я пошел"
        } else {
            return "Ты кто такой? Давай досвидания"
        }
    }

}