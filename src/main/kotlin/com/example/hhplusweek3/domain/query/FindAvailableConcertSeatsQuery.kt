package com.example.hhplusweek3.domain.query

import java.time.Instant

data class FindAvailableConcertSeatsQuery(
    val dateUtc: Instant
) {
    fun validate() {
        val now = Instant.now()
        require(dateUtc > now) { "$dateUtc should be greater than $now" }
    }
}
