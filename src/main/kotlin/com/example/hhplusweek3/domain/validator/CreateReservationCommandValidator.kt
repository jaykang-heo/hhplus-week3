package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.ConcertRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component

@Component
class CreateReservationCommandValidator(
    private val reservationRepository: ReservationRepository,
    private val queueRepository: QueueRepository,
    private val concertRepository: ConcertRepository
) {

    fun validate(command: CreateReservationCommand) {
        command.validate()
        val queue = queueRepository.findByToken(command.token)
            ?: throw RuntimeException("${command.token} not found")

        if (queue.status != QueueStatus.ACTIVE) {
            throw RuntimeException("queue status must be active ${queue.status}")
        }

        val isExists = concertRepository.existsByDateAndSeatNumber(command.dateUtc, command.seatNumber)
        if (!isExists) {
            throw RuntimeException("concert by date ${command.dateUtc} and seat number ${command.seatNumber} not found")
        }

        val alreadyReserved = reservationRepository.findReservationBySeatNumberAndDate(command.dateUtc, command.seatNumber)
        if (alreadyReserved != null) {
            throw RuntimeException("Seat ${command.seatNumber} by date ${command.dateUtc} already reserved")
        }

        val existingReservation = reservationRepository.findByToken(command.token)
        if (existingReservation != null) {
            throw RuntimeException("Token ${command.token} already reserved seat ${existingReservation.reservedSeat}, date ${command.dateUtc}")
        }
    }
}
