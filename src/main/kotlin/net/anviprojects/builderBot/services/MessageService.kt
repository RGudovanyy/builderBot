package net.anviprojects.builderBot.services

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/*
 * Сервис для формирования задачи (Task) или списка задач (задачи выстраиваются в строгий порядок, ожидается ответ после
 * выполнения всех) и передачи ее в очередь RabbitMQ.
 * За парсинг сообщения в задачу отвечает MessageProcessor. Цепь событий:
 * MessageListener -(Message)-> MessageProcessor -(Task)-> MessageService -(Task)-> AmqpTemplate
 */

@Service
class MessageService {

    @Autowired
    lateinit var amqpTemplate : AmqpTemplate




}