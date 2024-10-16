package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ReservationService(
    private val reservationRepository: ReservationRepository
) {

    fun deleteIfExpired(date: Instant, seatNumber: Long) {
        val now = Instant.now()
        val reservation = reservationRepository.findBySeatNumberAndDate(seatNumber, date) ?: return
        if (reservation.expirationTimeUtc < now && reservation.paymentId == null) {
            reservationRepository.deleteByReservationId(reservation.id)
        }
    }
}
