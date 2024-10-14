package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Concert

data class ConcertResponse(
    val availableSeats: List<ConcertSeatResponse>,
    val allSeats: List<ConcertSeatResponse>
) {
    constructor(concert: Concert) : this(
        concert.availableSeats.map { ConcertSeatResponse(it) },
        concert.allSeats.map { ConcertSeatResponse(it) }
    )
}
