package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.repository.jpa.ReservationJpaRepository
import com.example.hhplusweek3.repository.model.ReservationEntity
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ReservationRepositoryImpl(
    private val reservationJpaRepository: ReservationJpaRepository
) : ReservationRepository {

    override fun save(reservation: Reservation): Reservation {
        val dataModel = ReservationEntity(reservation)
        return reservationJpaRepository.save(dataModel).toModel()
    }

    override fun findReservationBySeatNumberAndDate(dateUtc: Instant, seatNumber: Long): Reservation? {
        return reservationJpaRepository.findByReservedDateUtcAndReservedSeatNumber(dateUtc, seatNumber)?.toModel()
    }

    override fun findAllByOrderNumberIsNullAndBeforeDate(dateUtc: Instant): List<Reservation> {
        return reservationJpaRepository.findAllByReservationIdIsNullAndExpirationTimeUtcIsBefore(dateUtc).map { it.toModel() }
    }

    override fun deleteAllByReservationIds(reservationIds: List<String>) {
        reservationJpaRepository.deleteAllByReservationIdIn(reservationIds)
    }

    override fun findByToken(token: String): Reservation? {
        return reservationJpaRepository.findByQueueToken(token)?.toModel()
    }

    override fun findAllByDate(dateUtc: Instant): List<Reservation> {
        return reservationJpaRepository.findAllByReservedDateUtc(dateUtc).map { it.toModel() }
    }

    override fun findAll(): List<Reservation> {
        return reservationJpaRepository.findAll().map { it.toModel() }
    }

    override fun findByTokenAndReservationId(token: String, reservationId: String): Reservation? {
        return reservationJpaRepository.findByReservationIdAndQueueToken(reservationId, token)?.toModel()
    }

    override fun getByTokenAndReservationId(token: String, reservationId: String): Reservation {
        return reservationJpaRepository.findByReservationIdAndQueueToken(reservationId, token)!!.toModel()
    }
}
