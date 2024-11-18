package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.domain.model.OutboxEventStatus
import com.example.hhplusweek3.repository.model.OutboxEventEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxJpaRepository : JpaRepository<OutboxEventEntity, Long> {
    fun findAllByStatus(status: OutboxEventStatus = OutboxEventStatus.PENDING): List<OutboxEventEntity>

    fun findByEventId(eventId: String): OutboxEventEntity?
}
