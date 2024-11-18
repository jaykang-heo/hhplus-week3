package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.port.OutboxRepository
import com.example.hhplusweek3.repository.jpa.OutboxJpaRepository
import com.example.hhplusweek3.repository.model.OutboxEventEntity
import org.springframework.stereotype.Repository

@Repository
class OutboxRepositoryImpl(
    private val outboxJpaRepository: OutboxJpaRepository,
) : OutboxRepository {
    override fun save(event: OutboxEvent) {
        val dataModel = OutboxEventEntity(event)
        outboxJpaRepository.save(dataModel)
    }

    override fun save(updatedEvents: List<OutboxEvent>) {
        val dataModels = updatedEvents.map { OutboxEventEntity(it) }
        outboxJpaRepository.saveAll(dataModels)
    }

    override fun findAllByProcessed(processed: Boolean): List<OutboxEvent> =
        outboxJpaRepository.findAllByProcessed(false).map { it.toModel() }

    override fun getByEventId(eventId: String): OutboxEvent = outboxJpaRepository.findByEventId(eventId)!!.toModel()
}
