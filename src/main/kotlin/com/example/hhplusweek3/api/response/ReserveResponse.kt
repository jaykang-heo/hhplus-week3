package com.example.hhplusweek3.api.response

import java.time.Instant

data class ReserveResponse(
    val id: Long,
    val userId: String,
    val status: String,
    val seatNumber: Int,
    val dateUtc: Instant,
    val createdAt: Instant
)
