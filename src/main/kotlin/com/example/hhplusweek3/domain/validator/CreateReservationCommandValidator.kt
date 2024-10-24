package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.AlreadyReservedException
import com.example.hhplusweek3.domain.model.exception.ConcertSeatNotFoundException
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component

@Component
class CreateReservationCommandValidator(
    private val queueRepository: QueueRepository,
    private val concertSeatRepository: ConcertSeatRepository,
    private val reservationRepository: ReservationRepository,
) {
    fun validate(command: CreateReservationCommand) {
        command.validate()
        val queue =
            queueRepository.findByToken(command.queueToken)
                ?: throw QueueNotFoundException(command.queueToken)

        if (queue.status != QueueStatus.ACTIVE) {
            throw InvalidQueueStatusException(queue.status)
        }

        val isExists = concertSeatRepository.existsByDateAndSeatNumber(command.dateUtc, command.seatNumber)
        if (!isExists) {
            throw ConcertSeatNotFoundException(command.dateUtc, command.seatNumber)
        }

        val alreadyReserved = reservationRepository.findReservationBySeatNumberAndDate(command.dateUtc, command.seatNumber)
        if (alreadyReserved != null) {
            throw AlreadyReservedException(command.dateUtc, command.seatNumber)
        }
    }
}
