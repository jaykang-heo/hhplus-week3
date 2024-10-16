package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Reservation
import java.time.Instant

interface ReservationRepository {
    fun save(reservation: Reservation): Reservation
    fun findReservationBySeatNumberAndDate(dateUtc: Instant, seatNumber: Long): Reservation?
    fun findAllByOrderNumberIsNullAndBeforeDate(dateUtc: Instant): List<Reservation>
    fun deleteAllByReservationIds(reservationIds: List<String>)
    fun deleteByReservationId(reservationId: String)
    fun findByToken(token: String): Reservation?
    fun findAllByDate(dateUtc: Instant): List<Reservation>
    fun findAll(): List<Reservation>
    fun findByTokenAndReservationId(token: String, reservationId: String): Reservation?
    fun getByTokenAndReservationId(token: String, reservationId: String): Reservation
    fun findBySeatNumberAndDate(seatNumber: Long, dateUtc: Instant): Reservation?
}
