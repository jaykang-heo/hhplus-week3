package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.OutboxEvent

interface OutboxEventPublisher {
    fun publish(event: OutboxEvent)
}
