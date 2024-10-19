package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import org.springframework.stereotype.Component

@Component
class GetWalletBalanceQueryValidator(
    private val queueRepository: QueueRepository
) {

    fun validate(query: GetWalletBalanceQuery) {
        query.validate()
        val queue = queueRepository.findByToken(query.queueToken)
            ?: throw RuntimeException("Queue not found by ${query.queueToken}")
        if (queue.status != QueueStatus.ACTIVE) {
            throw RuntimeException("Queue status is not active. ${queue.status}")
        }
    }
}
