package com.example.hhplusweek3.domain

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class QueueService(
    private val queueRepository: QueueRepository
) {

    fun generateQueue(command: IssueQueueTokenCommand): Queue {
        return Queue(command)
    }

    fun expireBeforeTime(time: Instant) {
        val queues = queueRepository.findAllByActiveAndBeforeTime(time)
        val tokens = queues.map { it.token }
        queueRepository.changeStatusToExpire(tokens)
    }
}
