package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.stereotype.Component

@Component
class ChargeWalletCommandValidator(
    private val queueRepository: QueueRepository,
) {
    fun validate(command: ChargeWalletCommand) {
        command.validate()

        val queue =
            queueRepository.findByToken(command.queueToken)
                ?: throw RuntimeException("queue not found by ${command.queueToken}")

        if (queue.status != QueueStatus.ACTIVE) {
            throw RuntimeException("queue is not active. queue status is ${queue.status}")
        }
    }
}
