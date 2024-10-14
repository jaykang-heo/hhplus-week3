package com.example.hhplusweek3.api.contract

import com.example.hhplusweek3.api.request.ReserveRequest
import com.example.hhplusweek3.api.response.FindAvailableDatesResponse
import com.example.hhplusweek3.api.response.FindAvailableSeatsResponse
import com.example.hhplusweek3.api.response.ReserveResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.Instant

interface ReservationController {

    @Operation(summary = "사용 가능한 좌석 찾기", description = "주어진 날짜에 대해 사용 가능한 좌석을 검색합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용 가능한 좌석 검색 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = FindAvailableSeatsResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @GetMapping("/seats")
    fun getAvailableSeats(
        @RequestParam("dateUtc") dateUtc: Instant
    ): FindAvailableSeatsResponse

    @Operation(summary = "사용 가능한 날짜 찾기", description = "예약 가능한 날짜를 검색합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용 가능한 날짜 검색 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = FindAvailableDatesResponse::class))]
            ),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @GetMapping("/dates")
    fun getAvailableDates(): FindAvailableDatesResponse

    @Operation(summary = "좌석 예약", description = "주어진 날짜와 시간에 대해 좌석을 예약합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "좌석 예약 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ReserveResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun reserve(
        @RequestBody request: ReserveRequest,
        @RequestHeader("Authorization") authorization: String
    ): ReserveResponse
}
