package com.example.hhplusweek3.domain.command

import com.example.hhplusweek3.api.request.ChargeWalletBalanceRequest

data class ChargeWalletCommand(
    val amount: Long,
    val queueToken: String
) {

    fun validate() {
        require(amount > 0) { "Amount must be positive" }
        require(queueToken.isNotEmpty()) { "Token must not be empty" }
    }

    constructor(request: ChargeWalletBalanceRequest, token: String) : this(
        request.amount,
        token
    )
}
