package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.model.Concert
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import com.example.hhplusweek3.domain.service.ConcertService
import com.example.hhplusweek3.domain.validator.GetAvailableConcertSeatsQueryValidator
import org.springframework.stereotype.Service

@Service
class ConcertFacade(
    private val concertService: ConcertService,
    private val getAvailableConcertSeatsQueryValidator: GetAvailableConcertSeatsQueryValidator
) {
    fun findAvailableSeats(query: FindAvailableConcertSeatsQuery): Concert {
        getAvailableConcertSeatsQueryValidator.validate(query)
        val availableSeats = concertService.getAvailableSeatsByDate(query.dateUtc)
        val allSeats = concertService.getAllSeatsByDate(query.dateUtc)
        val availableSchedule = Concert.Schedule(query.dateUtc, availableSeats)
        val allSchedule = Concert.Schedule(query.dateUtc, allSeats)
        return Concert(listOf(availableSchedule), listOf(allSchedule))
    }

    fun findAvailableDates(): Concert {
        val availableSchedules = concertService.getAvailableSchedules()
        val allSchedules = concertService.getAllSchedules()
        return Concert(availableSchedules, allSchedules)
    }
}
