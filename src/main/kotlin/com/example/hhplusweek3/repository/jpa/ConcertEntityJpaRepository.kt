package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.ConcertEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ConcertEntityJpaRepository : JpaRepository<ConcertEntity, Long> {
    fun findByDateUtcAndSeatNumber(dateUtc: Instant, seatNumber: Long): ConcertEntity?
}
