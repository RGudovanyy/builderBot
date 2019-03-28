package net.anviprojects.builderBot.services

import org.springframework.stereotype.Component

/**
 * Холдер для управления сервисами.
 * Нужен для того, чтобы их концентрировать в одном месте и не передавать в качестве аргументов конструктора
 */

@Component
class ServiceHolder (val messageProcessor : MessageProcessor,
                    val commonMessageService : CommonMessageService,
                    val systemMessageService : SystemMessageService,
                    val taskExecutorService : TaskExecutorService)