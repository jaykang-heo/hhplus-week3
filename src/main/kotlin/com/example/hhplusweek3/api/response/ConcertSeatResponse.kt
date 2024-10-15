package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.ConcertSeat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "콘서트 좌석 응답 객체")
data class ConcertSeatResponse(
    @Schema(
        description = "콘서트 날짜 및 시간 (UTC)",
        example = "2021-12-31T00:00:00Z"
    )
    val dateUtc: Instant,

    @Schema(
        description = "좌석 번호",
        example = "1"
    )
    val seatNumber: Long,

    @Schema(
        description = "가격",
        example = "100000"
    )
    val amount: Long
) {
    constructor(concertSeat: ConcertSeat) : this(
        concertSeat.dateUtc,
        concertSeat.seatNumber,
        concertSeat.amount
    )
}
