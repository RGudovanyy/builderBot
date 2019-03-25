package net.anviprojects.builderBot.tasks

import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.BuildPlan

class BuildTask(val buildPlan : BuildPlan, override val taskType: TaskType, val botUser : BotUser) : AbstractTask(taskType, botUser) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildTask

        if (buildPlan != other.buildPlan) return false
        if (taskType != other.taskType) return false
        if (botUser != other.botUser) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buildPlan.hashCode()
        result = 31 * result + taskType.hashCode()
        result = 31 * result + botUser.hashCode()
        return result
    }

    override fun toString(): String {
        return "Задача на $taskType ${buildPlan.name} на ${buildPlan.teamcity}"
    }
}