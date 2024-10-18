package com.example.hhplusweek3.api.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "좌석 예약 요청 객체")
data class ReserveRequest(
    @Schema(
        description = "좌석 번호",
        example = "1",
        required = true
    )
    val seatNumber: Long,

    @Schema(
        description = "예약 날짜 및 시간 (UTC)",
        example = "2021-12-31T23:59:59Z",
        required = true
    )
    val dateUtc: Instant
)
