package com.example.hhplusweek3.api

import com.example.hhplusweek3.application.OutboxEventFacade
import com.example.hhplusweek3.domain.command.MarkOutboxEventProcessedCommand
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class OutboxEventConsumer(
    private val outboxEventFacade: OutboxEventFacade,
) {
    @KafkaListener(topics = ["payment.created"])
    fun consume(
        @Payload payload: String,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String,
    ) {
        val command = MarkOutboxEventProcessedCommand(key)
        outboxEventFacade.execute(command)
    }
}
