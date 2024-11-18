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
    var status: OutboxEventStatus,
    var retryCount: Int = 0,
    var lastRetryAt: Instant?,
    var lastFailureReason: String?,
) {
    fun markProcessed(): OutboxEvent {
        this.status = OutboxEventStatus.PROCESSED
        this.processedAt = Instant.now()
        return this
    }

    fun incrementRetryCount() {
        retryCount++
        lastRetryAt = Instant.now()
    }

    constructor(payment: Payment, objectMapper: ObjectMapper) : this(
        UUID.randomUUID().toString(),
        OutboxEventType.PAYMENT,
        payment.paymentId,
        objectMapper.writeValueAsString(payment),
        Instant.now(),
        null,
        OutboxEventStatus.PENDING,
        0,
        null,
        null,
    )
}
