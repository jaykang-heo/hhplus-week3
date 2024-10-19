package com.example.hhplusweek3.domain.query

data class GetQueueQuery(
    val token: String
) {
    fun validate() {
        require(token.isNotBlank()) { "Token cannot be blank" }
    }
}
