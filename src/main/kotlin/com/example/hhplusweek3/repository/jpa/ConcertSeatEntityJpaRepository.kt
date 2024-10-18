package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ConcertSeatEntityJpaRepository : JpaRepository<ConcertSeatEntity, Long> {
    fun findByDateUtcAndSeatNumber(dateUtc: Instant, seatNumber: Long): ConcertSeatEntity?
    fun existsByDateUtc(dateUtc: Instant): Boolean
    fun findAllByDateUtc(dateUtc: Instant): List<ConcertSeatEntity>
}
