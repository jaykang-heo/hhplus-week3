package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.service.QueueService
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Transactional
class QueueActivationMaintenanceFacade(
    private val queueService: QueueService,
) {
    @Scheduled(fixedDelay = 60000)
    fun performMaintenance() {
        queueService.activatePendingQueues()
    }
}
