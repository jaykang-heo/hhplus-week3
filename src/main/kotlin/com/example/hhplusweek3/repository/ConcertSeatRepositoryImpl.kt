package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.ConcertSeat
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ConcertSeatRepositoryImpl(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository,
) : ConcertSeatRepository {
    @Cacheable(
        cacheNames = ["concertSeats"],
        key = "'exists:' + #dateUtc + ':' + #seatNumber",
    )
    override fun existsByDateAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): Boolean = concertSeatEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber) != null

    @Cacheable(
        cacheNames = ["concertSeats"],
        key = "'exists:' + #dateUtc",
    )
    override fun existsByDate(dateUtc: Instant): Boolean = concertSeatEntityJpaRepository.existsByDateUtc(dateUtc)

    @Cacheable(
        cacheNames = ["concertSeats"],
        key = "'list:' + #dateUtc",
    )
    override fun findByDate(dateUtc: Instant): List<ConcertSeat> =
        concertSeatEntityJpaRepository.findAllByDateUtc(dateUtc).map { it.toModel() }

    @Cacheable(
        cacheNames = ["concertSeats"],
        key = "'all'",
    )
    override fun findAll(): List<ConcertSeat> = concertSeatEntityJpaRepository.findAll().map { it.toModel() }

    @Cacheable(
        cacheNames = ["concertSeats"],
        key = "'seat:' + #dateUtc + ':' + #seatNumber",
    )
    override fun getByDateAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat = concertSeatEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber)!!.toModel()
}
