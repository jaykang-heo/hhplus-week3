package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetQueueQuery
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.service.WalletService
import com.example.hhplusweek3.domain.validator.GetQueueQueryValidator
import org.springframework.stereotype.Service

@Service
class QueueFacade(
    private val queueService: QueueService,
    private val walletService: WalletService,
    private val queueRepository: QueueRepository,
    private val getQueueQueryValidator: GetQueueQueryValidator,
) {
    fun issue(command: IssueQueueTokenCommand): Queue {
        val queue = queueService.generateQueue()
        queueRepository.save(queue)
        val queues = queueService.activatePendingQueues()
        walletService.createEmpty(queues)
        return queueRepository.getByToken(queue.token)
    }

    fun get(query: GetQueueQuery): Queue {
        getQueueQueryValidator.validate(query)
        return queueRepository.getByToken(query.token)
    }
}
