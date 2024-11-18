package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.MarkOutboxEventProcessedCommand
import com.example.hhplusweek3.domain.command.PublishOutboxEventCommand
import com.example.hhplusweek3.domain.command.RepublishOutboxEventsCommand
import com.example.hhplusweek3.domain.service.OutboxService
import org.springframework.stereotype.Service

@Service
class OutboxEventFacade(
    private val outboxService: OutboxService,
) {
    fun execute(command: PublishOutboxEventCommand) {
        val events = outboxService.findAllUnprocessedEvents()
        outboxService.publishAllAndUpdateEvents(events)
    }

    fun execute(command: MarkOutboxEventProcessedCommand) {
        val event = outboxService.findByEventId(command.eventId)
        val updatedEvent = event.markProcessed()
        outboxService.save(updatedEvent)
    }

    fun execute(command: RepublishOutboxEventsCommand) {
        val unpublishedEvents = outboxService.findAllUnprocessedEvents()
        val updatedEvents = outboxService.republishAllAndUpdateEvents(unpublishedEvents)
        outboxService.saveAll(updatedEvents)
    }
}
