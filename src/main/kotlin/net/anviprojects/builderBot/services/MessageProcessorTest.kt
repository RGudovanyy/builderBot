package net.anviprojects.builderBot.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class MessageProcessorTest(@Autowired val messageProcessor : MessageProcessor) {

    @Test
    fun `exclude link from message`(){
        val msg = "some_text <a href=http://some_link.domain.com>http://some_link.domain.com</a> some_text"
        val expected = "some_text http://some_link.domain.com some_text"
        assertEquals(messageProcessor.excludeLinkFromMessage(msg), expected)
    }
}