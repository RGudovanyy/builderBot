package net.anviprojects.builderBot.model

import net.anviprojects.builderBot.services.ServiceHolder
import net.anviprojects.builderBot.tasks.AbstractTask

class ConversationContext(private var botUser: BotUser,
                          val serviceHolder: ServiceHolder) {

    private var tasks = ArrayList<AbstractTask>()
    private var messages = ArrayList<Message>()
    private var lastMessageTime = 0L

    private fun clearContext(chat : MessengerChat, notify : Boolean) {
        if (tasks.isNotEmpty()) {
            tasks = ArrayList()
        }
        if (notify) {
            serviceHolder.commonMessageService.notifyClearContext(chat)
        }
    }

    fun addMessageToConversation(message: Message) {
        messages.add(message)
        lastMessageTime = message.sentTime
        val messageContentLower = message.content.toLowerCase()

        if (tasks.isNotEmpty() && serviceHolder.messageProcessor.isYes(messageContentLower)) {
            serviceHolder.taskExecutorService.excuteTasks(tasks, message.chat)
            message.chat.sendMessage("Океюшки")
            clearContext(message.chat, false)
            return
        } else if (tasks.isNotEmpty() && serviceHolder.messageProcessor.isNo(messageContentLower)) {
            clearContext(message.chat, true)
            return
        }

        val taskType = serviceHolder.messageProcessor.identifyTaskType(messageContentLower)
        if (taskType != null && !serviceHolder.messageProcessor.isSystemMessage(messageContentLower)) {
            val tasksFromMessage = serviceHolder.messageProcessor.createTasks(messageContentLower, botUser, taskType)
            tasks.addAll(tasksFromMessage)

            serviceHolder.commonMessageService.askForSubmit(message.chat, tasks)

        } else if (serviceHolder.messageProcessor.isSystemMessage(messageContentLower)) {
            serviceHolder.systemMessageService.onMessage(message, botUser)

        } else {
            serviceHolder.commonMessageService.sendNotUnderstandMessage(message.chat)
        }
    }
}