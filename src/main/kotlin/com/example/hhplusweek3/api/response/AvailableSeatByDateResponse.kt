package com.example.hhplusweek3.api.response

import java.time.Instant

data class AvailableSeatByDateResponse(
    val dateUtc: Instant,
    val availableSeats: List<Int>,
    val allSeats: List<Int>
)
