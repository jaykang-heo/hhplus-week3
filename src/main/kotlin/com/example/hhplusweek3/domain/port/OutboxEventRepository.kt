package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.OutboxEvent

interface OutboxEventRepository {
    fun save(event: OutboxEvent)

    fun saveAll(updatedEvents: List<OutboxEvent>)

    fun findAllByUnprocessed(): List<OutboxEvent>

    fun getByEventId(eventId: String): OutboxEvent
}
