package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.ReservationService
import com.example.hhplusweek3.domain.validator.CreateReservationCommandValidator
import org.springframework.stereotype.Service

@Service
class ReservationFacade(
    private val reservationService: ReservationService,
    private val createReservationCommandValidator: CreateReservationCommandValidator,
    private val reservationRepository: ReservationRepository,
    private val concertSeatRepository: ConcertSeatRepository,
) {
    fun reserve(command: CreateReservationCommand): Reservation =
        reservationService.reserveWithLockOrThrow(command) {
            createReservationCommandValidator.validate(command)
            val concertSeatAmount = concertSeatRepository.getByDateAndSeatNumber(command.dateUtc, command.seatNumber).amount
            val reservation = Reservation(command, concertSeatAmount)
            reservationRepository.save(reservation)
        }
}
