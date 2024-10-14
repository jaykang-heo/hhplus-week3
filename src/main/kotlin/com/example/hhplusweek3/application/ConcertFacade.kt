package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.model.Concert
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import com.example.hhplusweek3.domain.validator.GetAvailableConcertSeatsQueryValidator
import org.springframework.stereotype.Service

@Service
class ConcertFacade(
    private val concertSeatRepository: ConcertSeatRepository,
    private val reservationRepository: ReservationRepository,
    private val getAvailableConcertSeatsQueryValidator: GetAvailableConcertSeatsQueryValidator
) {

    fun findAvailableSeats(query: FindAvailableConcertSeatsQuery): Concert {
        getAvailableConcertSeatsQueryValidator.validate(query)
        val reservedSeats = reservationRepository.findAllByDate(query.dateUtc).map { it.reservedSeat }
        val allConcertSeats = concertSeatRepository.findByDate(query.dateUtc)
        val availableConcertSeats = allConcertSeats.filter { !reservedSeats.contains(it.seatNumber) }
        return Concert(availableConcertSeats, allConcertSeats, listOf(), listOf())
    }

    fun findAvailableDates(): Concert {
        val reservedSeats = reservationRepository.findAll().map { it.reservedSeat }
        val allConcertSeats = concertSeatRepository.findAll()
        val availableConcertDates = allConcertSeats.filter { !reservedSeats.contains(it.seatNumber) }.map { it.dateUtc }.distinct()
        val allConcertDates = allConcertSeats.map { it.dateUtc }.distinct()
        return Concert(listOf(), listOf(), availableConcertDates, allConcertDates)
    }
}
