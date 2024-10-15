package com.example.hhplusweek3.api.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용 가능한 좌석 응답 객체")
data class FindAvailableSeatsResponse(
    @Schema(description = "콘서트 정보", implementation = ConcertResponse::class)
    val concert: ConcertResponse
)
