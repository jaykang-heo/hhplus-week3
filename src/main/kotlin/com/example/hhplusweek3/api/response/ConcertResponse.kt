package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Concert
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "콘서트 응답 객체")
data class ConcertResponse(
    @Schema(description = "사용 가능한 좌석 리스트", implementation = ConcertSeatResponse::class)
    val availableSeats: List<ConcertSeatResponse>,

    @Schema(description = "전체 좌석 리스트", implementation = ConcertSeatResponse::class)
    val allSeats: List<ConcertSeatResponse>
) {
    constructor(concert: Concert) : this(
        concert.availableSeats.map { ConcertSeatResponse(it) },
        concert.allSeats.map { ConcertSeatResponse(it) }
    )
}
