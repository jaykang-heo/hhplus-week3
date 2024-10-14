package com.example.hhplusweek3.domain.model

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import java.time.Instant
import java.util.UUID

data class Reservation(
    val reservationId: String,
    val paymentId: String?,
    val queueToken: String,
    val dateTimeUtc: Instant,
    val reservedSeat: Long,
    val amount: Long,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant,
    val expirationTimeUtc: Instant
) {
    constructor(command: CreateReservationCommand, amount: Long) : this(
        UUID.randomUUID().toString(),
        null,
        command.token,
        command.dateUtc,
        command.seatNumber,
        amount,
        Instant.now(),
        Instant.now(),
        Instant.now().plusSeconds(60 * 5)
    )
}
