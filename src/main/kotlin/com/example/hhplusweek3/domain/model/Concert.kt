package com.example.hhplusweek3.domain.model

import java.time.Instant

data class Concert(
    val name: String,
    val dateUtcBySeat: Map<Instant, Long>
)
