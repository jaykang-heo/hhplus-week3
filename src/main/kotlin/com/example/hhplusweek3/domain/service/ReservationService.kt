package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import jakarta.transaction.Transactional
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val concertSeatRepository: ConcertSeatRepository,
) {
    @Transactional
    fun reserveWithPessimisticLock(
        command: CreateReservationCommand,
        action: () -> Reservation,
    ): Reservation {
        concertSeatRepository.getByDateAndSeatNumberWithPessimisticLockOrThrow(command.dateUtc, command.seatNumber)
        return action.invoke()
    }

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 50, multiplier = 2.0, maxDelay = 1000),
    )
    fun reserveWithOptimisticLock(
        command: CreateReservationCommand,
        action: () -> Reservation,
    ): Reservation {
        concertSeatRepository.getByDateAndSeatNumberWithOptimisticLockOrThrow(command.dateUtc, command.seatNumber)
        return action.invoke()
    }

    @Transactional
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

    @Transactional
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
