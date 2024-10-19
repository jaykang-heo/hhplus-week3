package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.service.ReservationService
import org.springframework.stereotype.Component

@Component
class CreateReservationCommandValidator(
    private val queueRepository: QueueRepository,
    private val concertSeatRepository: ConcertSeatRepository,
    private val reservationService: ReservationService,
) {
    fun validate(command: CreateReservationCommand) {
        command.validate()
        val queue =
            queueRepository.findByToken(command.token)
                ?: throw RuntimeException("${command.token} not found")

        if (queue.status != QueueStatus.ACTIVE) {
            throw RuntimeException("queue status must be active ${queue.status}")
        }

        val isExists = concertSeatRepository.existsByDateAndSeatNumber(command.dateUtc, command.seatNumber)
        if (!isExists) {
            throw RuntimeException("concert by date ${command.dateUtc} and seat number ${command.seatNumber} not found")
        }

        val isValid = reservationService.isValid(command.dateUtc, command.seatNumber, command.token)
        if (!isValid) {
            throw RuntimeException("Cannot make reservation for ${command.dateUtc} and seat number ${command.seatNumber}.")
        }
    }
}
