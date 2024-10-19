package com.example.hhplusweek3.domain.command

import java.time.Instant

data class CreateReservationCommand(
    val token: String,
    val seatNumber: Long,
    val dateUtc: Instant
) {
    fun validate() {
        val now = Instant.now()
        require(token.isNotBlank()) { "token must not be blank" }
        require(seatNumber > 0) { "seatNumber must not be greater than zero" }
        require(dateUtc > now) { "dateUtc cannot be in the past than $now" }
    }
}
