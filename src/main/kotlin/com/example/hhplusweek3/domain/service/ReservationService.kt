package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ReservationService(
    private val reservationRepository: ReservationRepository,
) {
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
