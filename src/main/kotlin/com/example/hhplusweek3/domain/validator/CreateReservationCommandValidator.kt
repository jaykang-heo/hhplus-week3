package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.ConcertSeatNotFoundException
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.InvalidReservationException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
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
                ?: throw QueueNotFoundException(command.token)

        if (queue.status != QueueStatus.ACTIVE) {
            throw InvalidQueueStatusException(queue.status)
        }

        val isExists = concertSeatRepository.existsByDateAndSeatNumber(command.dateUtc, command.seatNumber)
        if (!isExists) {
            throw ConcertSeatNotFoundException(command.dateUtc, command.seatNumber)
        }

        val isValid = reservationService.isValid(command.dateUtc, command.seatNumber, command.token)
        if (!isValid) {
            throw InvalidReservationException(command.dateUtc, command.seatNumber, command.token)
        }
    }
}
