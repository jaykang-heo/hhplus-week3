package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.Concert
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ConcertService(
    private val concertSeatRepository: ConcertSeatRepository,
    private val reservationRepository: ReservationRepository,
) {
    fun getAvailableSchedules(): List<Concert.Schedule> {
        val now = Instant.now()
        val allConcertSeats =
            concertSeatRepository
                .findAll()
                .filter { it.dateUtc.isAfter(now) }

        val reservedSeats =
            reservationRepository
                .findAll()
                .map { it.reservedSeat to it.dateTimeUtc }
                .toSet()

        val availableSeats =
            allConcertSeats.filter { seat ->
                (seat.seatNumber to seat.dateUtc) !in reservedSeats
            }

        val groupedAvailableSeats = availableSeats.groupBy { it.dateUtc }

        return groupedAvailableSeats.map { (date, seats) ->
            Concert.Schedule(
                date = date,
                seats = seats.map { Concert.Schedule.Seat(number = it.seatNumber) },
            )
        }
    }

    fun getAllSchedules(): List<Concert.Schedule> {
        val allConcertSeats = concertSeatRepository.findAll()

        val groupedAllSeats = allConcertSeats.groupBy { it.dateUtc }

        return groupedAllSeats.map { (date, seats) ->
            Concert.Schedule(
                date = date,
                seats = seats.map { Concert.Schedule.Seat(number = it.seatNumber) },
            )
        }
    }

    fun getAvailableSeatsByDate(date: Instant): List<Concert.Schedule.Seat> {
        val now = Instant.now()
        if (date.isBefore(now)) {
            return emptyList()
        }

        val allSeats = concertSeatRepository.findByDate(date)

        val reservedSeats =
            reservationRepository
                .findAllByDate(date)
                .map { it.reservedSeat }
                .toSet()

        val availableSeats =
            allSeats.filter { seat ->
                seat.seatNumber !in reservedSeats
            }

        return availableSeats.map { Concert.Schedule.Seat(number = it.seatNumber) }
    }

    fun getAllSeatsByDate(date: Instant): List<Concert.Schedule.Seat> {
        val allSeats = concertSeatRepository.findByDate(date)
        return allSeats.map { Concert.Schedule.Seat(number = it.seatNumber) }
    }
}
