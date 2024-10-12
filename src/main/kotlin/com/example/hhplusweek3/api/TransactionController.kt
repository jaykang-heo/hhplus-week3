package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.request.ChargeBalanceRequest
import com.example.hhplusweek3.api.response.ChargeBalanceResponse
import com.example.hhplusweek3.api.response.GetBalanceResponse
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import kotlin.random.Random

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController {
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전하고 거래 세부 정보를 반환합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "충전 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ChargeBalanceResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 입력"),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "충전 오류")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTransaction(
        @RequestBody request: ChargeBalanceRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ChargeBalanceResponse {
        return ChargeBalanceResponse(
            UUID.randomUUID().toString(),
            Random.nextLong(1, Long.MAX_VALUE)
        )
    }

    @Operation(summary = "잔액 조회", description = "사용자의 현재 잔액을 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GetBalanceResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @GetMapping
    fun getBalance(@RequestHeader("Authorization") authHeader: String): GetBalanceResponse {
        return GetBalanceResponse(
            UUID.randomUUID().toString(),
            Random.nextLong(1, Long.MAX_VALUE)
        )
    }
}
