package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.ReservationEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface ReservationEntityJpaRepository : JpaRepository<ReservationEntity, Long> {
    fun findByReservedDateUtcAndReservedSeatNumber(
        dateUtc: Instant,
        seatNumber: Long,
    ): ReservationEntity?

    fun findAllByReservationIdIsNullAndExpirationTimeUtcIsBefore(dateUtc: Instant): List<ReservationEntity>

    fun deleteAllByReservationIdIn(reservationIds: List<String>)

    fun deleteByReservationId(reservationId: String)

    fun findByQueueToken(queueToken: String): ReservationEntity?

    fun findAllByReservedDateUtc(dateUtc: Instant): List<ReservationEntity>

    fun findByReservationIdAndQueueToken(
        reservationId: String,
        queueToken: String,
    ): ReservationEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ReservationEntity r  where r.queueToken = :queueToken and r.reservationId = :reservationId")
    fun findByReservationIdAndQueueTokenWithLock(
        @Param("reservationId") reservationId: String,
        @Param("queueToken") queueToken: String,
    ): ReservationEntity?
}
