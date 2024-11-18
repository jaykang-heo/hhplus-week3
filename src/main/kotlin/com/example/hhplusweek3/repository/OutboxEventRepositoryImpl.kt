package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.OutboxEvent
import com.example.hhplusweek3.domain.port.OutboxEventRepository
import com.example.hhplusweek3.repository.jpa.OutboxJpaRepository
import com.example.hhplusweek3.repository.model.OutboxEventEntity
import org.springframework.stereotype.Repository

@Repository
class OutboxEventRepositoryImpl(
    private val outboxJpaRepository: OutboxJpaRepository,
) : OutboxEventRepository {
    override fun save(event: OutboxEvent) {
        val dataModel = OutboxEventEntity(event)
        outboxJpaRepository.save(dataModel)
    }

    override fun saveAll(updatedEvents: List<OutboxEvent>) {
        val dataModels = updatedEvents.map { OutboxEventEntity(it) }
        outboxJpaRepository.saveAll(dataModels)
    }

    override fun findAllByUnprocessed(): List<OutboxEvent> = outboxJpaRepository.findAllByStatus().map { it.toModel() }

    override fun getByEventId(eventId: String): OutboxEvent = outboxJpaRepository.findByEventId(eventId)!!.toModel()
}
