package com.example.hhplusweek3.domain.service

import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Transactional
class QueueMaintenanceService(
    private val queueService: QueueService
) {
    @Scheduled(fixedDelay = 60000)
    fun performMaintenance() {
        queueService.expireBeforeTime(Instant.now())
        queueService.activatePendingQueues()
    }
}
