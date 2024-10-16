package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Concert
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "사용 가능한 날짜 응답 객체")
data class FindAvailableDatesResponse(
    @Schema(
        description = "사용 가능한 날짜의 UTC 리스트",
        example = "[\"2021-12-31T00:00:00Z\"]"
    )
    val availableDatesUtc: List<Instant>,

    @Schema(
        description = "전체 날짜의 UTC 리스트",
        example = "[\"2021-12-30T00:00:00Z\", \"2021-12-31T00:00:00Z\"]"
    )
    val allDatesUtc: List<Instant>
) {
    constructor(concert: Concert) : this(
        availableDatesUtc = concert.availableSchedules.map { it.date }.distinct(),
        allDatesUtc = concert.allSchedules.map { it.date }.distinct()
    )
}
