package net.anviprojects.builderBot.model

import net.anviprojects.builderBot.services.CommonMessageService
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.SystemMessageService
import net.anviprojects.builderBot.tasks.AbstractTask

class ConversationContext(private var botUser: BotUser,
                          val messageProcessor: MessageProcessor,
                          val commonMessageService: CommonMessageService,
                          val systemMessageService : SystemMessageService) {

    private var tasks = ArrayList<AbstractTask>()
    private var messages = ArrayList<Message>()
    private var lastMessageTime = 0L
    private var delayTime = 0L  //

    private fun clearContext(chat : MessengerChat, notify : Boolean) {
        if (tasks.isNotEmpty()) {
            tasks = ArrayList()
        }
        if (notify) {
            commonMessageService.notifyClearContext(chat)
        }
    }

    fun addMessageToConversation(message: Message) {
        messages.add(message)
        lastMessageTime = message.sentTime
        val messageContentLower = message.content.toLowerCase()

        if (tasks.isNotEmpty() && messageProcessor.isYes(messageContentLower)) {
            //sendTasksToMessageService(tasks)
            message.chat.sendMessage("Океюшки")
            clearContext(message.chat, false)
            return
        } else if (tasks.isNotEmpty() && messageProcessor.isNo(messageContentLower)) {
            clearContext(message.chat, true)
            return
        }

        val taskType = messageProcessor.identifyTaskType(messageContentLower)
        if (taskType != null && !messageProcessor.isSystemMessage(messageContentLower)) {
            val tasksFromMessage = messageProcessor.createTasks(messageContentLower, botUser, taskType)
            tasks.addAll(tasksFromMessage)

            commonMessageService.askForSubmit(message.chat, tasks)

        } else if (messageProcessor.isSystemMessage(messageContentLower)) {
            systemMessageService.onMessage(message, botUser)

        } else {
            commonMessageService.sendNotUnderstandMessage(message.chat)
        }
    }
}