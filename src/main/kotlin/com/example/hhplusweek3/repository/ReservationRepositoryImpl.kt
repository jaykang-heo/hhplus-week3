package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.repository.jpa.ReservationEntityJpaRepository
import com.example.hhplusweek3.repository.model.ReservationEntity
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ReservationRepositoryImpl(
    private val reservationEntityJpaRepository: ReservationEntityJpaRepository,
) : ReservationRepository {
    override fun save(reservation: Reservation): Reservation {
        val dataModel = ReservationEntity(reservation)
        return reservationEntityJpaRepository.save(dataModel).toModel()
    }

    override fun findReservationBySeatNumberAndDate(
        dateUtc: Instant,
        seatNumber: Long,
    ): Reservation? = reservationEntityJpaRepository.findByReservedDateUtcAndReservedSeatNumber(dateUtc, seatNumber)?.toModel()

    override fun findAllByOrderNumberIsNullAndBeforeDate(dateUtc: Instant): List<Reservation> =
        reservationEntityJpaRepository.findAllByReservationIdIsNullAndExpirationTimeUtcIsBefore(dateUtc).map {
            it.toModel()
        }

    override fun deleteAllByReservationIds(reservationIds: List<String>) {
        reservationEntityJpaRepository.deleteAllByReservationIdIn(reservationIds)
    }

    override fun deleteByReservationId(reservationId: String) {
        reservationEntityJpaRepository.deleteByReservationId(reservationId)
    }

    override fun findByToken(token: String): Reservation? = reservationEntityJpaRepository.findByQueueToken(token)?.toModel()

    override fun findAllByDate(dateUtc: Instant): List<Reservation> =
        reservationEntityJpaRepository.findAllByReservedDateUtc(dateUtc).map {
            it.toModel()
        }

    override fun findAll(): List<Reservation> = reservationEntityJpaRepository.findAll().map { it.toModel() }

    override fun findByTokenAndReservationId(
        token: String,
        reservationId: String,
    ): Reservation? = reservationEntityJpaRepository.findByReservationIdAndQueueToken(reservationId, token)?.toModel()

    override fun getByTokenAndReservationId(
        token: String,
        reservationId: String,
    ): Reservation = reservationEntityJpaRepository.findByReservationIdAndQueueToken(reservationId, token)!!.toModel()

    override fun findBySeatNumberAndDate(
        seatNumber: Long,
        dateUtc: Instant,
    ): Reservation? = reservationEntityJpaRepository.findByReservedDateUtcAndReservedSeatNumber(dateUtc, seatNumber)?.toModel()
}
