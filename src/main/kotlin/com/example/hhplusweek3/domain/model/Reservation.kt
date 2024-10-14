package com.example.hhplusweek3.domain.model

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import java.time.Instant
import java.util.UUID

data class Reservation(
    val reservationId: String,
    val orderNumber: String?,
    val queueToken: String,
    val dateTimeUtc: Instant,
    val reservedSeat: Long,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant,
    val expirationTimeUtc: Instant
) {
    constructor(command: CreateReservationCommand) : this(
        UUID.randomUUID().toString(),
        null,
        command.token,
        command.dateUtc,
        command.seatNumber,
        Instant.now(),
        Instant.now(),
        Instant.now().plusSeconds(60 * 5)
    )
}
