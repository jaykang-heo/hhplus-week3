package com.example.hhplusweek3.domain.model

import java.io.Serializable
import java.time.Instant

data class ConcertSeat(
    val dateUtc: Instant,
    val seatNumber: Long,
    val amount: Long,
) : Serializable
