package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import org.springframework.stereotype.Component

@Component
class GetWalletBalanceQueryValidator(
    private val queueRepository: QueueRepository,
) {
    fun validate(query: GetWalletBalanceQuery) {
        query.validate()
        val queue =
            queueRepository.findByToken(query.queueToken)
                ?: throw QueueNotFoundException(query.queueToken)

        if (queue.status != QueueStatus.ACTIVE) {
            throw InvalidQueueStatusException(queue.status)
        }
    }
}
