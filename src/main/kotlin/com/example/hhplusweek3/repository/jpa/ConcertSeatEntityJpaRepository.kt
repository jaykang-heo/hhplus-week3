package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.ConcertSeatEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface ConcertSeatEntityJpaRepository : JpaRepository<ConcertSeatEntity, Long> {
    fun findByDateUtcAndSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeatEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ConcertSeatEntity c  where c.dateUtc = :dateUtc and c.seatNumber = :seatNumber")
    fun findByDateUtcAndSeatNumberWithLock(
        @Param("dateUtc") dateUtc: Instant,
        @Param("seatNumber") seatNumber: Long,
    ): ConcertSeatEntity?

    fun existsByDateUtc(dateUtc: Instant): Boolean

    fun findAllByDateUtc(dateUtc: Instant): List<ConcertSeatEntity>
}
