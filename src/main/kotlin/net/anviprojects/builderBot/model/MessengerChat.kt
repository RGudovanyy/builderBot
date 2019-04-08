package net.anviprojects.builderBot.model

import com.microsoft.bot.schema.models.ResourceResponse

interface MessengerChat {

    //fun sendMessage(chatMessage: String)

    fun sendMessage(chatMessage: String) : ResourceResponse
}