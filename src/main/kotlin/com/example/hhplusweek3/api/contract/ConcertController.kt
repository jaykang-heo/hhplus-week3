package com.example.hhplusweek3.api.contract

import com.example.hhplusweek3.api.response.FindAvailableDatesResponse
import com.example.hhplusweek3.api.response.FindAvailableSeatsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant

interface ConcertController {

    @Operation(
        summary = "사용 가능한 좌석 찾기",
        description = "주어진 날짜에 대해 사용 가능한 좌석을 검색합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용 가능한 좌석 검색 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = FindAvailableSeatsResponse::class)
                    )
                ]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @GetMapping("/seats")
    fun findAvailableSeats(
        @Parameter(
            description = "UTC 형식의 날짜 (예: 2021-12-31T00:00:00Z)",
            required = true,
            example = "2021-12-31T00:00:00Z"
        )
        @RequestParam("dateUtc")
        dateUtc: Instant
    ): FindAvailableSeatsResponse

    @Operation(
        summary = "사용 가능한 날짜 찾기",
        description = "예약 가능한 날짜를 검색합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용 가능한 날짜 검색 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = FindAvailableDatesResponse::class)
                    )
                ]
            ),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @GetMapping("/dates")
    fun findAvailableDates(): FindAvailableDatesResponse
}
