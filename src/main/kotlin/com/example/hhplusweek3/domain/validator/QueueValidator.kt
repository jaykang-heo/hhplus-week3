package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.stereotype.Component

@Component
class QueueValidator(
    private val queueRepository: QueueRepository,
) {
    fun isValid(queueToken: String): Boolean {
        val queue =
            queueRepository.findByToken(queueToken)
                ?: return false

        return queue.status == QueueStatus.ACTIVE
    }
}
