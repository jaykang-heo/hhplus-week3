package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Concert
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "콘서트 스케줄 응답 객체")
data class ScheduleResponse(
    @Schema(
        description = "콘서트 날짜 및 시간 (UTC)",
        example = "2021-12-31T00:00:00Z"
    )
    val dateUtc: Instant,

    @Schema(description = "좌석 리스트", implementation = SeatResponse::class)
    val seats: List<SeatResponse>
) {
    constructor(schedule: Concert.Schedule) : this(
        dateUtc = schedule.date,
        seats = schedule.seats.map { SeatResponse(it) }
    )
}
