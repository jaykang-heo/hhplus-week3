package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.ConcertSeat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(
    name = "concert_seats",
    indexes = [
        Index(name = "idx_concert_seats_date", columnList = "dateUtc"),
        Index(name = "idx_concert_seats_date_seat", columnList = "dateUtc,seatNumber"),
    ],
)
class ConcertSeatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val dateUtc: Instant,
    val seatNumber: Long,
    val amount: Long,
) {
    fun toModel(): ConcertSeat =
        ConcertSeat(
            dateUtc,
            seatNumber,
            amount,
        )
}
