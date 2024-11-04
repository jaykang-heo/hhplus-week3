package com.example.hhplusweek3.repository.redis

import com.example.hhplusweek3.repository.model.ReservationEntity
import org.springframework.data.repository.CrudRepository
import java.time.Instant

interface ReservationEntityRepository : CrudRepository<ReservationEntity, String> {
    fun findByQueueToken(queueToken: String): ReservationEntity?

    fun findAllByReservedDateUtc(dateUtc: Instant): List<ReservationEntity>

    fun findByReservationId(reservationId: String): ReservationEntity?
}
