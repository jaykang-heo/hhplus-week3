package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.port.OutboxEventPublisher
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Repository

@Repository
class OutboxEventPublisherImpl(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : OutboxEventPublisher {
    private val logger = KotlinLogging.logger(this::class.java.name)

    override fun publish(event: OutboxEvent) {
        try {
            kafkaTemplate
                .send(
                    "${event.aggregateType}.${event.aggregateType}",
                    event.aggregateId,
                    event.payload,
                ).get()
        } catch (e: Exception) {
            logger.error("Failed to publish event: ${event.eventId}", e)
        }
    }
}
