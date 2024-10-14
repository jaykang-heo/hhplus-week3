package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.ConcertSeatStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "concert_seats")
class ConcertSeatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val concertDateUtc: Instant,
    @Enumerated(EnumType.STRING)
    val status: ConcertSeatStatus,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant
)
