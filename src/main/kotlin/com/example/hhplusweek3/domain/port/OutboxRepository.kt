package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.OutboxEvent

interface OutboxRepository {
    fun save(event: OutboxEvent)

    fun save(updatedEvents: List<OutboxEvent>)

    fun findAllByProcessed(processed: Boolean): List<OutboxEvent>

    fun getByEventId(eventId: String): OutboxEvent
}
