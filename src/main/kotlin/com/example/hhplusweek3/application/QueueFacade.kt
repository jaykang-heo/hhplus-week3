package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetQueueQuery
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.validator.GetQueueQueryValidator
import com.example.hhplusweek3.domain.validator.IssueQueueTokenCommandValidator
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class QueueFacade(
    private val queueService: QueueService,
    private val queueRepository: QueueRepository,
    private val issueQueueTokenCommandValidator: IssueQueueTokenCommandValidator,
    private val getQueueQueryValidator: GetQueueQueryValidator
) {

    fun issue(command: IssueQueueTokenCommand): Queue {
        val queue = queueService.generateQueue(command)
        queueService.expireBeforeTime(queue.createdTimeUtc)
        issueQueueTokenCommandValidator.validate(command)
        return queueRepository.save(queue)
    }

    fun get(query: GetQueueQuery): Queue {
        val now = Instant.now()
        query.validate()
        queueService.expireBeforeTime(now)
        getQueueQueryValidator.validate(query)
        return queueRepository.getByToken(query.token)
    }
}
