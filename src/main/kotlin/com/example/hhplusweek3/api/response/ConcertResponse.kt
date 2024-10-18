package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Concert
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "콘서트 응답 객체")
data class ConcertResponse(
    @Schema(description = "사용 가능한 스케줄 리스트", implementation = ScheduleResponse::class)
    val availableSchedules: List<ScheduleResponse>,

    @Schema(description = "전체 스케줄 리스트", implementation = ScheduleResponse::class)
    val allSchedules: List<ScheduleResponse>
) {
    constructor(concert: Concert) : this(
        availableSchedules = concert.availableSchedules.map { ScheduleResponse(it) },
        allSchedules = concert.allSchedules.map { ScheduleResponse(it) }
    )
}
