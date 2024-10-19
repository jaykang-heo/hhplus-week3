package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Concert
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "콘서트 좌석 응답 객체")
data class SeatResponse(
    @Schema(description = "좌석 번호", example = "1")
    val number: Long,
) {
    constructor(seat: Concert.Schedule.Seat) : this(
        number = seat.number,
    )
}
