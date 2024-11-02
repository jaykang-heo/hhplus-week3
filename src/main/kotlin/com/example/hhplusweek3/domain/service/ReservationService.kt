package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.model.exception.AcquireLockFailedException
import com.example.hhplusweek3.domain.port.LockRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val lockRepository: LockRepository,
) {
    fun reserveWithLockOrThrow(
        command: CreateReservationCommand,
        action: () -> Reservation,
    ): Reservation =
        lockRepository.acquireReservationLock(command.dateUtc, command.seatNumber) {
            action.invoke()
        }
            ?: throw AcquireLockFailedException("Reservation lock failed::${command.dateUtc}, ${command.seatNumber}, ${command.queueToken}")

    fun deleteIfExpired(
        date: Instant,
        seatNumber: Long,
    ) {
        val now = Instant.now()
        val reservation = reservationRepository.findBySeatNumberAndDate(seatNumber, date) ?: return
        if (reservation.expirationTimeUtc < now && reservation.paymentId == null) {
            reservationRepository.deleteByReservationId(reservation.id)
        }
    }

    fun isValid(
        dateUtc: Instant,
        seatNumber: Long,
        queueToken: String,
    ): Boolean {
        val alreadyReserved = reservationRepository.findReservationBySeatNumberAndDate(dateUtc, seatNumber)
        if (alreadyReserved != null) {
            return false
        }

        val existingReservation = reservationRepository.findByToken(queueToken)
        if (existingReservation != null) {
            return false
        }
        return true
    }
}
