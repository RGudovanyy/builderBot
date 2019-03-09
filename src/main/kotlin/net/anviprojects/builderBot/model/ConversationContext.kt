package net.anviprojects.builderBot.model

import com.samczsun.skype4j.chat.Chat
import com.samczsun.skype4j.chat.messages.ChatMessage
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.MessageService
import net.anviprojects.builderBot.tasks.Task

class ConversationContext(private var startTime: Long, private var botUser: BotUser,
                          val messageProcessor: MessageProcessor, val messageService: MessageService) {

    private var tasks = ArrayList<Task>()
    private var messages = ArrayList<ChatMessage>()
    private var lastMessageTime = 0L
    private var delayTime = 0L  //

    private fun clearContext(chat : Chat, notify : Boolean) {
        if (tasks.isNotEmpty()) {
            tasks = ArrayList()
        }
        if (notify) {
            notifyClearContext(chat)
        }
    }

    fun addMessageToConversation(message: ChatMessage) {
        messages.add(message)
        lastMessageTime = message.sentTime

        if (tasks.isNotEmpty() && messageProcessor.isYes(message.content)) {
            //sendTasksToMessageService(tasks)
            message.chat.sendMessage("Океюшки")
            clearContext(message.chat, false)
        } else if (tasks.isNotEmpty() && messageProcessor.isNo(message.content)) {
            clearContext(message.chat, true)
        }

        if (messageProcessor.isTaskMessage(message.content)) {
            val tasksFromMessage = messageProcessor.createTasks(message.content, botUser)
            tasks.addAll(tasksFromMessage)

            askForSubmit(message.chat, tasks)

        } else if (messageProcessor.isSystemMessage(message.content)) {
            //TODO not implemented

        } else {
            //TODO not implemented

        }

    }

    private fun askForSubmit(chat: Chat, tasks: ArrayList<Task>){
        val res = StringBuilder().append("Выполняю:")
        tasks.stream().forEach { res.append("\n").append(it) }
        res.append("\nВсе верно?")
        chat.sendMessage(res.toString())
    }

    private fun notifyClearContext(chat: Chat) {
        chat.sendMessage("Все добавленные задачи отменены")
    }


}