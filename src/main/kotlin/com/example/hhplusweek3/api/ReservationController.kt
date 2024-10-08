package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.request.ReserveRequest
import com.example.hhplusweek3.api.response.AvailableSeatByDateResponse
import com.example.hhplusweek3.api.response.FindAvailableDatesResponse
import com.example.hhplusweek3.api.response.FindAvailableSeatsResponse
import com.example.hhplusweek3.api.response.ReserveResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID
import kotlin.random.Random

@RestController
@RequestMapping("/reservation")
class ReservationController {

    @GetMapping("/available/seats")
    fun findAvailableSeats(
        @RequestParam("dateUtc") dateUtc: Instant
    ): FindAvailableSeatsResponse {
        return FindAvailableSeatsResponse(
            (1..5).map {
                AvailableSeatByDateResponse(
                    Instant.now(),
                    (1..5).map { it },
                    (1..50).map { it }
                )
            }
        )
    }

    @GetMapping("/available/dates")
    fun findAvailableDates(): FindAvailableDatesResponse {
        return FindAvailableDatesResponse(
            (1..5).map { Instant.now() },
            (1..10).map { Instant.now() }
        )
    }

    @PostMapping("/reserve")
    fun reserve(
        @RequestBody request: ReserveRequest,
        @RequestHeader("Authorization") authorization: String
    ): ReserveResponse {
        return ReserveResponse(
            Random.nextLong(1, Long.MAX_VALUE),
            UUID.randomUUID().toString(),
            "PEDNING",
            Random.nextInt(1, Int.MAX_VALUE),
            Instant.now(),
            Instant.now()
        )
    }
}
