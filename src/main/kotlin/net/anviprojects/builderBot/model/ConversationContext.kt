package net.anviprojects.builderBot.model

import com.microsoft.bot.schema.models.ResourceResponse
import net.anviprojects.builderBot.services.ServiceHolder
import net.anviprojects.builderBot.tasks.AbstractTask

class ConversationContext(private var botUser: BotUser,
                          val serviceHolder: ServiceHolder) {

    private var tasks = ArrayList<AbstractTask>()
    private var messages = ArrayList<Message>()
    private var lastMessageTime = 0L

    private fun clearContext() : String {
        if (tasks.isNotEmpty()) {
            tasks = ArrayList()
        }
        return serviceHolder.commonMessageService.notifyClearContext()
    }

    fun addMessageToConversationAndReply(message: Message) : String {
        messages.add(message)
        lastMessageTime = message.sentTime
        val messageContentLower = message.content.toLowerCase()

        if (tasks.isNotEmpty() && serviceHolder.messageProcessor.isYes(messageContentLower)) {
            serviceHolder.taskExecutorService.executeTasks(tasks)
            return clearContext()

        } else if (tasks.isNotEmpty() && serviceHolder.messageProcessor.isNo(messageContentLower)) {
            return clearContext()

        }

        val taskType = serviceHolder.messageProcessor.identifyTaskType(messageContentLower)
        if (taskType != null && !serviceHolder.messageProcessor.isSystemMessage(messageContentLower)) {
            val tasksFromMessage = serviceHolder.messageProcessor.createTasks(messageContentLower, botUser, taskType)
            tasks.addAll(tasksFromMessage)

             return serviceHolder.commonMessageService.askForSubmit(tasks)

        } else if (serviceHolder.messageProcessor.isSystemMessage(messageContentLower)) {
            return serviceHolder.systemMessageService.onMessage(message, botUser)

        } else {
            return  serviceHolder.commonMessageService.sendNotUnderstandMessage()
        }
    }
}