package com.example.hhplusweek3.domain.port

import java.time.Instant

interface ConcertRepository {
    fun existsByDateAndSeatNumber(dateUtc: Instant, seatNumber: Long): Boolean
}
