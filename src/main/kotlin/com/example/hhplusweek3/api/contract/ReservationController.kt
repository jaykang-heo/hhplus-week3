package com.example.hhplusweek3.api.contract

import com.example.hhplusweek3.api.request.ReserveRequest
import com.example.hhplusweek3.api.response.ErrorResponse
import com.example.hhplusweek3.api.response.ReserveResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus

interface ReservationController {

    @Operation(
        summary = "좌석 예약",
        description = "주어진 날짜와 시간에 대해 좌석을 예약합니다.",
        tags = ["예약 관리"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "좌석 예약 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ReserveResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않음",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 오류",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun reserve(
        @Parameter(
            description = "예약 요청 정보",
            required = true,
            schema = Schema(implementation = ReserveRequest::class)
        )
        @RequestBody
        request: ReserveRequest,

        @Parameter(
            description = "인증 헤더 (예: {token})",
            required = true,
            example = "abcdef123456"
        )
        @RequestHeader("Authorization")
        authorization: String
    ): ReserveResponse
}
