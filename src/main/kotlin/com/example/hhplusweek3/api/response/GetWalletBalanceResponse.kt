package com.example.hhplusweek3.api.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "잔액 조회 응답 객체")
data class GetWalletBalanceResponse(
    @Schema(
        description = "현재 잔액",
        example = "100000"
    )
    val balance: Long
)
