package com.example.hhplusweek3.domain.model

data class Wallet(
    var balance: Long,
    val queueToken: String,
) {
    companion object {
        fun empty(queueToken: String): Wallet = Wallet(0, queueToken)
    }
}
