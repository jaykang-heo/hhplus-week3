package com.example.hhplusweek3.api.contract

import com.example.hhplusweek3.api.request.ChargeWalletBalanceRequest
import com.example.hhplusweek3.api.response.ChargeWalletBalanceResponse
import com.example.hhplusweek3.api.response.ErrorResponse
import com.example.hhplusweek3.api.response.GetWalletBalanceResponse
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

interface WalletController {
    @Operation(
        summary = "잔액 충전",
        description = "사용자의 잔액을 충전하고 거래 세부 정보를 반환합니다.",
        tags = ["지갑 관리"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "충전 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChargeWalletBalanceResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 입력",
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
                description = "충전 오류",
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
    fun chargeWalletBalance(
        @Parameter(
            description = "잔액 충전 요청",
            required = true,
            schema = Schema(implementation = ChargeWalletBalanceRequest::class)
        )
        @RequestBody
        request: ChargeWalletBalanceRequest,

        @Parameter(
            description = "인증 헤더 (예: {token})",
            required = true,
            example = "token_abcdef123456"
        )
        @RequestHeader("Authorization")
        authHeader: String
    ): ChargeWalletBalanceResponse

    @Operation(
        summary = "잔액 조회",
        description = "사용자의 현재 잔액을 조회합니다.",
        tags = ["지갑 관리"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GetWalletBalanceResponse::class)
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
    @GetMapping
    fun getWalletBalance(
        @Parameter(
            description = "인증 헤더 (예: {token})",
            required = true,
            example = "abcdef123456"
        )
        @RequestHeader("Authorization")
        authHeader: String
    ): GetWalletBalanceResponse
}
