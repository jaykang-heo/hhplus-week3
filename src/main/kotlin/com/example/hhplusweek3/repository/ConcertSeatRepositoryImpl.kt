package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.ConcertSeat
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ConcertSeatRepositoryImpl(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository
) : ConcertSeatRepository {
    override fun existsByDateAndSeatNumber(dateUtc: Instant, seatNumber: Long): Boolean {
        return concertSeatEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber) != null
    }

    override fun existsByDate(dateUtc: Instant): Boolean {
        return concertSeatEntityJpaRepository.existsByDateUtc(dateUtc)
    }

    override fun findByDate(dateUtc: Instant): List<ConcertSeat> {
        return concertSeatEntityJpaRepository.findAllByDateUtc(dateUtc).map { it.toModel() }
    }

    override fun findAll(): List<ConcertSeat> {
        return concertSeatEntityJpaRepository.findAll().map { it.toModel() }
    }

    override fun getByDateAndSeatNumber(dateUtc: Instant, seatNumber: Long): ConcertSeat {
        return concertSeatEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber)!!.toModel()
    }
}
