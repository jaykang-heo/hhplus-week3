package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.ConcertSeat
import java.time.Instant

interface ConcertSeatRepository {
    fun existsByDateAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): Boolean

    fun existsByDate(dateUtc: Instant): Boolean

    fun findByDate(dateUtc: Instant): List<ConcertSeat>

    fun findAll(): List<ConcertSeat>

    fun getByDateAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat

    fun getByDateAndSeatNumberWithPessimisticLockOrThrow(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat

    fun getByDateAndSeatNumberWithOptimisticLockOrThrow(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat
}
