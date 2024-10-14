package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.port.ConcertRepository
import com.example.hhplusweek3.repository.jpa.ConcertEntityJpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ConcertRepositoryImpl(
    private val concertEntityJpaRepository: ConcertEntityJpaRepository
) : ConcertRepository {
    override fun existsByDateAndSeatNumber(dateUtc: Instant, seatNumber: Long): Boolean {
        return concertEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber) != null
    }
}
