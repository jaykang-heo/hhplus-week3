package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.ConcertSeat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant

@Entity
@Table(name = "concert_seats")
class ConcertSeatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @Version
    val version: Long,
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
