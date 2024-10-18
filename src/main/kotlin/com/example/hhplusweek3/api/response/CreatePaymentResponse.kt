package com.example.hhplusweek3.api.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "결제 생성 응답 객체")
data class CreatePaymentResponse(
    @Schema(
        description = "결제 ID",
        example = "payment_1234567890"
    )
    val paymentId: String
)
