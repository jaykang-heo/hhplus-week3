package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.ReservationService
import com.example.hhplusweek3.domain.validator.CreateReservationCommandValidator
import jakarta.transaction.Transactional
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service

@Service
class ReservationFacade(
    private val reservationService: ReservationService,
    private val createReservationCommandValidator: CreateReservationCommandValidator,
    private val reservationRepository: ReservationRepository,
    private val concertSeatRepository: ConcertSeatRepository,
) {
    @Transactional
    fun reserve(command: CreateReservationCommand): Reservation =
        try {
            reservationService.reserveWithOptimisticLock(command) {
                reservationService.deleteIfExpired(command.dateUtc, command.seatNumber)
                createReservationCommandValidator.validate(command)
                val concertSeatAmount =
                    concertSeatRepository
                        .getByDateAndSeatNumberWithOptimisticLockOrThrow(
                            command.dateUtc,
                            command.seatNumber,
                        ).amount
                val reservation = Reservation(command, concertSeatAmount)
                reservationRepository.save(reservation)
            }
        } catch (e: OptimisticLockingFailureException) {
            throw IllegalStateException("Concurrent modification detected for seat ${command.seatNumber}")
        }
}
