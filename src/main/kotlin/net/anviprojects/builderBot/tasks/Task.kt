package net.anviprojects.builderBot.tasks

import net.anviprojects.builderBot.model.BotUser

class Task(val target: String, val taskType: TaskType, val user: BotUser) {

    override fun equals(other: Any?): Boolean {
        // пока будем считать, что таски равны в разрезе любого пользователя
        if (other is Task) {
            return this.target == other.target && this.taskType == other.taskType
        }
        return false
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + taskType.hashCode()
        result = 31 * result + user.hashCode()
        return result
    }

    override fun toString(): String {
        return "Задача на $taskType $target"
    }
}