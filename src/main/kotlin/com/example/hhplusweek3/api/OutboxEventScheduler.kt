package com.example.hhplusweek3.api

import com.example.hhplusweek3.application.OutboxEventFacade
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxEventScheduler(
    private val outboxEventFacade: OutboxEventFacade,
) {
    @Scheduled(fixedDelay = 1_000)
    fun run() {
        outboxEventFacade.execute()
    }
}
