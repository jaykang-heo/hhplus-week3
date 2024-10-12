package com.example.hhplusweek3.api.request

import java.time.Instant

data class ReserveRequest(
    val seatNumber: Long,
    val dateUct: Instant
)
