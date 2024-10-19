package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Wallet
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "잔액 충전 응답 객체")
data class ChargeWalletBalanceResponse(
    @Schema(
        description = "충전 후 잔액",
        example = "150000"
    )
    val balance: Long
) {
    constructor(wallet: Wallet) : this(wallet.balance)
}
