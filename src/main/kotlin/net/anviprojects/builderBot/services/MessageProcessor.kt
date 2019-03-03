package net.anviprojects.builderBot.services

import org.springframework.stereotype.Component


/*
 * Вспомогательный класс который разбирает входящий Message и на основе его формирует одну или список Task
 * Парсинг производится по следующим правилам:
 * если в сообщении есть слово "сборка" - то будет создана задача с типом BUILD, следующее слово после него будет целью задачи
 * если после цели стоит "и" или ","  - то будет создана еще одна задача с типом BUILD
 * если в сообщении есть слово начинающееся на "обновл" - то будет создана задача с типом DEPLOY
 * допускается так же вариант когда "и" или "," разделяет 2 пары задач на сборку и обновление
 *
 * Примеры:
 * Сборка master - формируется Task (master, TaskType.BUILD, User)
 * Сборка master и stable - формируется Task(master, TaskType.BUILD, User) и Task(stable, TaskType.BUILD, User)
 * Сборка master с обновлением masterStand - формируется Task(master, TaskType.BUILD, User) и Task(masterStand, TaskType.DEPLOY, User)
 *
 */
@Component
class MessageProcessor {

}
