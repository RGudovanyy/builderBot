package net.anviprojects.builderBot.services

import net.anviprojects.builderBot.model.MessengerChat
import net.anviprojects.builderBot.tasks.AbstractTask

interface TaskExecutorService {

    fun excuteTasks(tasks : List<AbstractTask>, chat : MessengerChat) {


    }

}