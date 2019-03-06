package net.anviprojects.builderBot.tasks

enum class TaskType {
    BUILD {
        override fun toString(): String {
            return "сборку"
        }
    },
    DEPLOY {
        override fun toString(): String {
            return "обновление"
        }
    },
    REBOOT {
        override fun toString(): String {
            return "перезагрузку"
        }
    }
}