package net.anviprojects.builderBot.model

data class Message(val content : String, val sentTime : Long, val sender : String, val chat: MessengerChat)