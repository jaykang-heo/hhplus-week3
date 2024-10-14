package com.example.hhplusweek3.api.contract

import com.example.hhplusweek3.api.request.ChargeWalletBalanceRequest
import com.example.hhplusweek3.api.response.ChargeWalletBalanceResponse
import com.example.hhplusweek3.api.response.GetWalletBalanceResponse
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

@RestController
@RequestMapping("/api/v1/wallets")
interface WalletController {
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전하고 거래 세부 정보를 반환합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "충전 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ChargeWalletBalanceResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 입력"),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "충전 오류")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun chargeWalletBalance(
        @RequestBody request: ChargeWalletBalanceRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ChargeWalletBalanceResponse

    @Operation(summary = "잔액 조회", description = "사용자의 현재 잔액을 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GetWalletBalanceResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @GetMapping
    fun getWalletBalance(@RequestHeader("Authorization") authHeader: String): GetWalletBalanceResponse
}
