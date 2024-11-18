package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.OutboxEvent
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
    val processed: Boolean,
) {
    fun toModel(): OutboxEvent =
        OutboxEvent(
            eventId,
            aggregateType,
            aggregateId,
            payload,
            createdAt,
            processedAt,
            processed,
        )

    constructor(event: OutboxEvent) : this(
        0L,
        event.eventId,
        event.aggregateType,
        event.aggregateId,
        event.payload,
        event.createdAt,
        event.processedAt,
        event.processed,
    )
}
