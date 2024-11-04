package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.Reservation
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

@RedisHash("reservations")
class ReservationEntity(
    @Id
    val reservationId: String,
    val paymentId: String?,
    @Indexed
    val queueToken: String,
    val reservedSeatNumber: Long,
    @Indexed
    val reservedDateUtc: Instant,
    val amount: Long,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant,
    val expirationTimeUtc: Instant,
    @TimeToLive(unit = TimeUnit.SECONDS)
    val ttl: Long =
        Duration
            .between(Instant.now(), expirationTimeUtc)
            .toSeconds()
            .coerceAtLeast(1),
) {
    fun toModel(): Reservation =
        Reservation(
            reservationId,
            paymentId,
            queueToken,
            reservedDateUtc,
            reservedSeatNumber,
            amount,
            createdTimeUtc,
            updatedTimeUtc,
            expirationTimeUtc,
        )

    constructor(reservation: Reservation) : this(
        reservation.id,
        reservation.paymentId,
        reservation.queueToken,
        reservation.reservedSeat,
        reservation.dateTimeUtc,
        reservation.amount,
        reservation.createdTimeUtc,
        reservation.updatedTimeUtc,
        reservation.expirationTimeUtc,
    )
}
