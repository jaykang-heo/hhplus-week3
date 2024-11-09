package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.repository.model.ReservationEntity
import com.example.hhplusweek3.repository.redis.ReservationEntityRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ReservationRepositoryImpl(
    private val reservationEntityRepository: ReservationEntityRepository,
) : ReservationRepository {
    override fun save(reservation: Reservation): Reservation {
        val dataModel = ReservationEntity(reservation)
        val temp = reservationEntityRepository.save(dataModel).toModel()
        return temp
    }

    override fun findReservationBySeatNumberAndDate(
        dateUtc: Instant,
        seatNumber: Long,
    ): Reservation? =
        reservationEntityRepository
            .findAllByReservedDateUtc(dateUtc)
            .firstOrNull { it.reservedSeatNumber == seatNumber }
            ?.toModel()

    override fun findAllByDate(dateUtc: Instant): List<Reservation> =
        reservationEntityRepository.findAllByReservedDateUtc(dateUtc).map {
            it.toModel()
        }

    override fun findAll(): List<Reservation> = reservationEntityRepository.findAll().map { it.toModel() }

    override fun findByTokenAndReservationId(
        token: String,
        reservationId: String,
    ): Reservation? =
        reservationEntityRepository
            .findByReservationId(reservationId)
            ?.let { if (it.queueToken == token) it else null }
            ?.toModel()

    override fun getByTokenAndReservationId(
        token: String,
        reservationId: String,
    ): Reservation = findByTokenAndReservationId(token, reservationId)!!
}
