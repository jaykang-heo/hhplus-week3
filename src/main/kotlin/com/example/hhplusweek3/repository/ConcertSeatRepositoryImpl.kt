package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.ConcertSeat
import com.example.hhplusweek3.domain.model.exception.ConcertSeatNotFoundException
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ConcertSeatRepositoryImpl(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository,
) : ConcertSeatRepository {
    override fun existsByDateAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): Boolean = concertSeatEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber) != null

    override fun existsByDate(dateUtc: Instant): Boolean = concertSeatEntityJpaRepository.existsByDateUtc(dateUtc)

    override fun findByDate(dateUtc: Instant): List<ConcertSeat> =
        concertSeatEntityJpaRepository.findAllByDateUtc(dateUtc).map { it.toModel() }

    override fun findAll(): List<ConcertSeat> = concertSeatEntityJpaRepository.findAll().map { it.toModel() }

    override fun getByDateAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat = concertSeatEntityJpaRepository.findByDateUtcAndSeatNumber(dateUtc, seatNumber)!!.toModel()

    override fun getByDateAndSeatNumberWithPessimisticLockOrThrow(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat =
        concertSeatEntityJpaRepository.findByDateUtcAndSeatNumberWithPessimisticLock(dateUtc, seatNumber)?.toModel()
            ?: throw ConcertSeatNotFoundException(dateUtc, seatNumber)

    override fun getByDateAndSeatNumberWithOptimisticLockOrThrow(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat =
        concertSeatEntityJpaRepository.findByDateUtcAndSeatNumberWithOptimisticLock(dateUtc, seatNumber)?.toModel()
            ?: throw ConcertSeatNotFoundException(dateUtc, seatNumber)
}
