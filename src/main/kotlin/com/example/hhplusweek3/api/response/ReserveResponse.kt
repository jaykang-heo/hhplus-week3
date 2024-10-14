package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Reservation
import java.time.Instant

data class ReserveResponse(
    val paymentId: String,
    val orderNumber: String?,
    val reservedSeatNumber: Long,
    val reservedTimeUtc: Instant,
    val queueToken: String,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant,
    val expirationTimeUtc: Instant
) {
    constructor(reservation: Reservation) : this(
        reservation.reservationId,
        reservation.paymentId,
        reservation.reservedSeat,
        reservation.dateTimeUtc,
        reservation.queueToken,
        reservation.createdTimeUtc,
        reservation.updatedTimeUtc,
        reservation.expirationTimeUtc
    )
}
