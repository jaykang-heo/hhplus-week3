package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.port.OutboxEventPublisher
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OutboxEventPublisherImpl(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : OutboxEventPublisher {
    private val logger = KotlinLogging.logger {}

    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0),
    )
    @Transactional
    override fun publish(event: OutboxEvent): OutboxEvent {
        try {
            val topic = "${event.aggregateType}.${event.aggregateId}"
            logger.info { "Publishing event to topic: $topic, eventId: ${event.eventId}" }

            kafkaTemplate
                .send(topic, event.aggregateId, event.payload)
                .get()
            logger.info { "Successfully published event: ${event.eventId}" }
            return event
        } catch (e: Exception) {
            event.incrementRetryCount()
            event.lastFailureReason = e.message
            logger.error(e) { "Failed to publish event: ${event.eventId}" }
            return event
        }
    }

    override fun publishToDeadLetterQueue(event: OutboxEvent): OutboxEvent =
        try {
            val dlqTopic = "${event.aggregateType}.${event.aggregateId}.dlq"
            kafkaTemplate
                .send(dlqTopic, event.aggregateId, event.payload)
                .get()
            event
        } catch (e: Exception) {
            event.incrementRetryCount()
            event.lastFailureReason = e.message
            event
        }
}
