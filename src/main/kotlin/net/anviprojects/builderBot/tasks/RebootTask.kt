package net.anviprojects.builderBot.tasks

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.WebLogic

class RebootTask(val webLogic: WebLogic, override val taskType: TaskType, val botUser: BotUser) : AbstractTask(taskType, botUser) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RebootTask

        if (webLogic != other.webLogic) return false
        if (taskType != other.taskType) return false
        if (botUser != other.botUser) return false

        return true
    }

    override fun hashCode(): Int {
        var result = webLogic.hashCode()
        result = 31 * result + taskType.hashCode()
        result = 31 * result + botUser.hashCode()
        return result
    }

    override fun toString(): String {
        return "Задача на $taskType ${webLogic.aliases.get(0)}"
    }
}