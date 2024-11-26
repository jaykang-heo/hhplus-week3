package com.example.hhplusweek3.api

import com.example.hhplusweek3.application.OutboxEventFacade
import com.example.hhplusweek3.domain.command.MarkOutboxEventProcessedCommand
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class OutboxEventConsumer(
    private val outboxEventFacade: OutboxEventFacade,
) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(
        topics = ["payment.created"],
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(
        @Payload payload: String,
        @Header("kafka_receivedMessageKey") key: String,
        acknowledgment: Acknowledgment,
    ) {
        try {
            logger.info { "Received payment.created event with key: $key" }
            val command = MarkOutboxEventProcessedCommand(key)
            outboxEventFacade.execute(command)
            acknowledgment.acknowledge()
            logger.info { "Successfully processed payment.created event with key: $key" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to process payment.created event with key: $key" }
            throw e
        }
    }
}
