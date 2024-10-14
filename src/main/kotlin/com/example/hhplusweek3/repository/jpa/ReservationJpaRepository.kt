package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long> {
    fun findByReservedDateUtcAndReservedSeatNumber(dateUtc: Instant, seatNumber: Long): ReservationEntity?
    fun findAllByOrderNumberIsNullAndExpirationTimeUtcIsBefore(dateUtc: Instant): List<ReservationEntity>
    fun deleteAllByReservationIdIn(reservationIds: List<String>)
    fun findByQueueToken(queueToken: String): ReservationEntity?
    fun findAllByReservedDateUtc(dateUtc: Instant): List<ReservationEntity>
}
