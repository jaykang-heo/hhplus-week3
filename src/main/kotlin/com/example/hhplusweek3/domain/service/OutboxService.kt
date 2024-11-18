package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.model.OutboxEventStatus
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.OutboxEventPublisher
import com.example.hhplusweek3.domain.port.OutboxEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class OutboxService(
    private val outboxEventRepository: OutboxEventRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    fun save(payment: Payment) {
        val event = OutboxEvent(payment, objectMapper)
        outboxEventRepository.save(event)
    }

    fun save(event: OutboxEvent) {
        outboxEventRepository.save(event)
    }

    fun saveAll(events: List<OutboxEvent>) {
        outboxEventRepository.saveAll(events)
    }

    fun findAllUnprocessedEvents(): List<OutboxEvent> = outboxEventRepository.findAllByUnprocessed()

    fun publishAllAndUpdateEvents(events: List<OutboxEvent>) {
        events.forEach { event -> outboxEventPublisher.publish(event) }
    }

    fun findByEventId(eventId: String): OutboxEvent = outboxEventRepository.getByEventId(eventId)

    fun republishAllAndUpdateEvents(events: List<OutboxEvent>): List<OutboxEvent> =
        events.map { event ->
            if (event.retryCount < MAX_RETRY_COUNT) {
                outboxEventPublisher.publishToDeadLetterQueue(event)
            } else {
                event.status = OutboxEventStatus.FAILED
                event.lastFailureReason = "Max retries exceeded"
                event
            }
        }

    companion object {
        const val MAX_RETRY_COUNT = 3
    }
}
