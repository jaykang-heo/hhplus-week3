package com.example.hhplusweek3.api.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "잔액 충전 요청 객체")
data class ChargeWalletBalanceRequest(
    @Schema(
        description = "충전할 금액",
        example = "50000",
        required = true,
        minimum = "1"
    )
    val amount: Long
)
