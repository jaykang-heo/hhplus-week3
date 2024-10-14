package com.example.hhplusweek3.domain.query

data class GetWalletBalanceQuery(
    val queueToken: String
) {
    fun validate() {
        require(queueToken.isNotBlank()) { "QueueToken cannot be blank" }
    }
}
