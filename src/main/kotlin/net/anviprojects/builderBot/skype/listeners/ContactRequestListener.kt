package net.anviprojects.builderBot.skype.listeners

import com.samczsun.skype4j.events.EventHandler
import com.samczsun.skype4j.events.Listener
import com.samczsun.skype4j.events.contact.ContactRequestEvent

class ContactRequestListener : Listener {

    @EventHandler
    fun onContact(event : ContactRequestEvent) {
        val newContact = event.request.sender.username
        event.request.accept()
        println("New contact request from $newContact was accepted")
    }
}