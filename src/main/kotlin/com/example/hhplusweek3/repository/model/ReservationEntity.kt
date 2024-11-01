package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.Reservation
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Version
import java.time.Instant

@Entity
@Table(
    name = "reservations",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_reservations_date_utc_seat_number",
            columnNames = ["reserved_date_utc", "reserved_seat_number"],
        ),
    ],
)
class ReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @Version
    val version: Long,
    val reservationId: String,
    val paymentId: String?,
    val queueToken: String,
    val reservedSeatNumber: Long,
    val reservedDateUtc: Instant,
    val amount: Long,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant,
    val expirationTimeUtc: Instant,
) {
    fun toModel(): Reservation =
        Reservation(
            reservationId,
            paymentId,
            queueToken = queueToken,
            reservedDateUtc,
            reservedSeatNumber,
            amount,
            createdTimeUtc,
            updatedTimeUtc,
            expirationTimeUtc,
        )

    constructor(reservation: Reservation) : this(
        0,
        0,
        reservation.id,
        reservation.paymentId,
        queueToken = reservation.queueToken,
        reservation.reservedSeat,
        reservation.dateTimeUtc,
        reservation.amount,
        reservation.createdTimeUtc,
        reservation.updatedTimeUtc,
        reservation.expirationTimeUtc,
    )
}
