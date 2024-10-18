package com.example.hhplusweek3.api.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "결제 생성 요청 객체")
data class CreatePaymentRequest(
    @Schema(
        description = "예약 ID",
        example = "reservation_1234567890",
        required = true
    )
    val reservationId: String
)
