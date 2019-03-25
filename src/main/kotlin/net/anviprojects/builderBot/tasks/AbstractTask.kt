package net.anviprojects.builderBot.tasks

import net.anviprojects.builderBot.model.BotUser

abstract class AbstractTask(open val taskType: TaskType, val user: BotUser)