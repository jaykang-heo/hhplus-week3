package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.contract.ConcertController
import com.example.hhplusweek3.api.response.ConcertResponse
import com.example.hhplusweek3.api.response.FindAvailableDatesResponse
import com.example.hhplusweek3.api.response.FindAvailableSeatsResponse
import com.example.hhplusweek3.application.ConcertFacade
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/concerts")
class ConcertControllerImpl(
    private val concertFacade: ConcertFacade
) : ConcertController {

    override fun findAvailableSeats(dateUtc: Instant): FindAvailableSeatsResponse {
        val query = FindAvailableConcertSeatsQuery(dateUtc)
        val concert = concertFacade.findAvailableSeats(query)
        return FindAvailableSeatsResponse(ConcertResponse(concert))
    }

    override fun findAvailableDates(): FindAvailableDatesResponse {
        val concert = concertFacade.findAvailableDates()
        return FindAvailableDatesResponse(concert.availableDates, concert.allDates)
    }
}
