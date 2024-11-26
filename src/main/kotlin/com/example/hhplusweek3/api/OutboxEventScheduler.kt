package com.example.hhplusweek3.api

import com.example.hhplusweek3.application.OutboxEventFacade
import com.example.hhplusweek3.domain.command.PublishOutboxEventCommand
import com.example.hhplusweek3.domain.command.RepublishOutboxEventsCommand
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxEventScheduler(
    private val outboxEventFacade: OutboxEventFacade,
) {
    @Scheduled(fixedDelay = 1_000)
    fun run() {
        val command = PublishOutboxEventCommand()
        outboxEventFacade.execute(command)
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    fun retryFailedEvents() {
        val command = RepublishOutboxEventsCommand()
        outboxEventFacade.execute(command)
    }
}
