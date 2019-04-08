package net.anviprojects.builderBot.services

import kotlinx.coroutines.*
import net.anviprojects.builderBot.model.MessengerChat
import net.anviprojects.builderBot.tasks.AbstractTask
import org.springframework.stereotype.Service

@Service
class TaskExecutorService {

    fun executeTasks(tasks : List<AbstractTask>) {
        // TODO возможно сюда нужен коллбэк для ответа пользователю

        GlobalScope.launch {
            for (task in tasks) {
                if (executeTaskInternal(task)) {
                    println("Задача [$task] успешно выполнена")
                } else {
                    println("Задача [$task] не выполнена")
                    break
                }
            }
        }

    }

    suspend fun executeTaskInternal(task: AbstractTask): Boolean {
        delay(2000)
        // тут мы будем дергать джарку
        return true
    }


}