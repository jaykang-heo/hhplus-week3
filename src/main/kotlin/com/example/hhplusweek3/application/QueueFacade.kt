package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.IssueQueueTokenCommandValidator
import com.example.hhplusweek3.domain.QueueService
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.stereotype.Service

@Service
class QueueFacade(
    private val queueService: QueueService,
    private val queueRepository: QueueRepository,
    private val issueQueueTokenCommandValidator: IssueQueueTokenCommandValidator
) {

    fun issue(command: IssueQueueTokenCommand): Queue {
        val queue = queueService.generateQueue(command)
        queueService.expireBeforeTime(queue.createdTimeUtc)
        issueQueueTokenCommandValidator.validate(command)
        return queueRepository.save(queue)
    }
}
