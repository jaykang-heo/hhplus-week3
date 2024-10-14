package com.example.hhplusweek3.domain.model

import java.time.Instant

data class ConcertSeat(
    val dateUtc: Instant,
    val seatNumber: Long
)
