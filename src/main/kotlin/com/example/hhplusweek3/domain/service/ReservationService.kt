package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ReservationService(
    private val reservationRepository: ReservationRepository
) {

    fun preRun() {
        deleteExpiredReservations()
    }

    fun deleteExpiredReservations() {
        val now = Instant.now()
        val expiredReservations = reservationRepository.findAllByOrderNumberIsNullAndBeforeDate(now)
        val reservationIds = expiredReservations.map { it.reservationId }
        if (reservationIds.isEmpty()) return
        reservationRepository.deleteAllByReservationIds(reservationIds)
    }
}
