package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
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
                ?: throw QueueNotFoundException(command.queueToken)

        if (queue.status != QueueStatus.ACTIVE) {
            throw InvalidQueueStatusException(queue.status)
        }
    }
}
