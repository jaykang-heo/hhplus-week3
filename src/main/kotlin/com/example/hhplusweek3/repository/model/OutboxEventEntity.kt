package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.model.OutboxEventStatus
import com.example.hhplusweek3.domain.model.OutboxEventType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "outbox_events")
class OutboxEventEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val sequence: Long,
    val eventId: String,
    @Enumerated(EnumType.STRING)
    val aggregateType: OutboxEventType,
    val aggregateId: String,
    val payload: String,
    val createdAt: Instant,
    var processedAt: Instant?,
    @Enumerated(EnumType.STRING)
    var status: OutboxEventStatus,
    var retryCount: Int = 0,
    var lastRetryAt: Instant?,
    var lastFailureReason: String?,
) {
    fun toModel(): OutboxEvent =
        OutboxEvent(
            eventId,
            aggregateType,
            aggregateId,
            payload,
            createdAt,
            processedAt,
            status,
            retryCount,
            lastRetryAt,
            lastFailureReason,
        )

    constructor(event: OutboxEvent) : this(
        0L,
        event.eventId,
        event.aggregateType,
        event.aggregateId,
        event.payload,
        event.createdAt,
        event.processedAt,
        event.status,
        event.retryCount,
        event.lastRetryAt,
        event.lastFailureReason,
    )
}
