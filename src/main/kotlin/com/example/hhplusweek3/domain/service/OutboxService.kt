package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.OutboxEventPublisher
import com.example.hhplusweek3.domain.port.OutboxRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class OutboxService(
    private val outboxRepository: OutboxRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    fun save(payment: Payment) {
        val event = OutboxEvent(payment, objectMapper)
        outboxRepository.save(event)
    }

    fun save(event: OutboxEvent) {
        outboxRepository.save(event)
    }

    fun findAllUnprocessedEvents(): List<OutboxEvent> = outboxRepository.findAllByProcessed(false)

    fun publish(event: OutboxEvent) = outboxEventPublisher.publish(event)

    fun findByEventId(eventId: String): OutboxEvent = outboxRepository.getByEventId(eventId)
}
