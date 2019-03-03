package net.anviprojects.builderBot.tasks

import net.anviprojects.builderBot.model.BotUser

data class Task(val target : String, val taskType : TaskType, val user : BotUser)