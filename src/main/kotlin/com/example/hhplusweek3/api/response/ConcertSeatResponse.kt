package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.ConcertSeat
import java.time.Instant

data class ConcertSeatResponse(
    val dateUtc: Instant,
    val seatNumber: Long
) {
    constructor(concertSeat: ConcertSeat) : this(
        concertSeat.dateUtc,
        concertSeat.seatNumber
    )
}
