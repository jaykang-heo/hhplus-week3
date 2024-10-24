package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.math.abs

@Component
class QueueService(
    private val queueRepository: QueueRepository,
) {
    fun generateQueue(command: IssueQueueTokenCommand): Queue = Queue(command)

    fun extendExpirationTime(queueToken: String) {
        val queue = queueRepository.findByToken(queueToken) ?: return
        val now = Instant.now()
        queue.extendExpirationTime(now)
        queue.updateUpdatedTime(now)
        queueRepository.update(queue)
    }

    fun expireBeforeTime(time: Instant) {
        val queues = queueRepository.findAllByActiveAndBeforeTime(time)
        val tokens = queues.map { it.token }
        queueRepository.changeStatusToExpire(tokens)
    }

    fun activatePendingQueues(): List<Queue> {
        val pendingQueues =
            queueRepository
                .findAllPending()
                .sortedBy { it.createdTimeUtc }
        val activeQueueCount = queueRepository.findAllActive().size
        val availableSlots = abs(ACTIVE_LIMIT - activeQueueCount)

        return pendingQueues
            .take(availableSlots)
            .map { queue ->
                queueRepository.changeStatusToActive(queue.token)
            }
    }

    companion object {
        const val ACTIVE_LIMIT = 100
    }
}
