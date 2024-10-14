package com.example.hhplusweek3.api.contract

import CreatePaymentRequest
import com.example.hhplusweek3.api.response.CreatePaymentResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

interface PaymentController {
    @Operation(summary = "결제 처리", description = "결제 요청을 처리하고 거래 세부 정보를 반환합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CreatePaymentResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 입력"),
            ApiResponse(responseCode = "401", description = "인증되지 않음"),
            ApiResponse(responseCode = "500", description = "결제 처리 오류")
        ]
    )
    @PostMapping("/payments")
    fun createPayment(
        @Parameter(description = "결제 요청 세부 정보") @RequestBody request: CreatePaymentRequest,
        @Parameter(description = "인증 헤더")
        @RequestHeader("Authorization")
        authHeader: String
    ): CreatePaymentResponse
}
