package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component

@Component
class CreatePaymentCommandValidator(
    private val reservationRepository: ReservationRepository,
    private val queueRepository: QueueRepository
) {

    fun validate(command: CreatePaymentCommand) {
        command.validate()

        val queue = queueRepository.findByToken(command.queueToken)
            ?: throw RuntimeException("queue token not found. ${command.queueToken}")

        if (queue.status != QueueStatus.ACTIVE) {
            throw RuntimeException("queue is not active. ${queue.status}")
        }

        reservationRepository.findByTokenAndReservationId(command.queueToken, command.reservationId)
            ?: throw RuntimeException("reservation token not found by ${command.queueToken} and ${command.reservationId}")
    }
}
