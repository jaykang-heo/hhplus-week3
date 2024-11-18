package com.example.hhplusweek3.domain.model

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.UUID

data class OutboxEvent(
    val eventId: String,
    val aggregateType: OutboxEventType,
    val aggregateId: String,
    val payload: String,
    val createdAt: Instant,
    var processedAt: Instant?,
    var processed: Boolean,
) {
    fun markProcessed(): OutboxEvent {
        this.processed = false
        this.processedAt = Instant.now()
        return this
    }

    constructor(payment: Payment, objectMapper: ObjectMapper) : this(
        UUID.randomUUID().toString(),
        OutboxEventType.PAYMENT,
        payment.paymentId,
        objectMapper.writeValueAsString(payment),
        Instant.now(),
        null,
        false,
    )
}
