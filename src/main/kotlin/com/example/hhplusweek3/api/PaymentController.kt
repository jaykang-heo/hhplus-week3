package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.request.ChargeBalanceRequest
import com.example.hhplusweek3.api.request.PayRequest
import com.example.hhplusweek3.api.response.ChargeBalanceResponse
import com.example.hhplusweek3.api.response.GetBalanceResponse
import com.example.hhplusweek3.api.response.PayResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import kotlin.random.Random

@RestController
@RequestMapping("/payment")
class PaymentController {

    @Operation(summary = "결제 처리", description = "결제 요청을 처리하고 거래 세부 정보를 반환합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = PayResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 입력"),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "결제 처리 오류")
        ]
    )
    @PostMapping("/pay")
    fun pay(
        @Parameter(description = "결제 요청 세부 정보") @RequestBody request: PayRequest,
        @Parameter(description = "인증 헤더")
        @RequestHeader("Authorization")
        authHeader: String
    ): PayResponse {
        return PayResponse(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )
    }

    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전하고 거래 세부 정보를 반환합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "충전 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ChargeBalanceResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 입력"),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "충전 오류")
        ]
    )
    @PostMapping("/charge")
    fun chargeBalance(
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
    @GetMapping("/balance")
    fun getBalance(
        @RequestHeader("Authorization") authHeader: String
    ): GetBalanceResponse {
        return GetBalanceResponse(
            UUID.randomUUID().toString(),
            Random.nextLong(1, Long.MAX_VALUE)
        )
    }
}
