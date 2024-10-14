package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetQueueQuery
import org.springframework.stereotype.Component

@Component
class GetQueueQueryValidator(
    private val queueRepository: QueueRepository
) {

    fun validate(query: GetQueueQuery) {
        queueRepository.findByToken(query.token)
            ?: throw RuntimeException("queue not found by ${query.token}")
    }
}
