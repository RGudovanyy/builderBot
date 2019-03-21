package net.anviprojects.builderBot.telegram

import net.anviprojects.builderBot.model.MessengerChat
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class TelegramChat (val chatId : String, val telegramFacade: TelegramFacade) : MessengerChat{
    override fun sendMessage(chatMessage: String) {
        telegramFacade.execute(SendMessage().setChatId(this.chatId).setText(chatMessage))
    }


}