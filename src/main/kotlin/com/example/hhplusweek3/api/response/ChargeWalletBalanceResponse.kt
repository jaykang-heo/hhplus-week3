package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Wallet

data class ChargeWalletBalanceResponse(
    val balance: Long
) {
    constructor(wallet: Wallet) : this(wallet.balance)
}
