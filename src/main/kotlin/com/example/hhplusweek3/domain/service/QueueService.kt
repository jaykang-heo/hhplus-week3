package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class QueueService(
    private val queueRepository: QueueRepository,
) {
    fun generateQueue(): Queue = Queue()

    fun expireBeforeTime(time: Instant) {
        queueRepository.expireBeforeTime(time)
    }

    fun activatePendingQueues(): List<Queue> {
        val activeQueueCount = queueRepository.countActiveQueues()
        val availableSlots = ACTIVE_LIMIT - activeQueueCount
        if (availableSlots <= 0) return listOf()

        val tokens = queueRepository.findPendingTokens(availableSlots.toLong())
        return queueRepository.activatePendingQueues(tokens)
    }

    companion object {
        const val ACTIVE_LIMIT = 100
    }
}
