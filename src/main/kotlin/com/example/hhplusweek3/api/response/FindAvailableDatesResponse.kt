package com.example.hhplusweek3.api.response

import java.time.Instant

data class FindAvailableDatesResponse(
    val availableDateUtcList: List<Instant>,
    val allDateUtcList: List<Instant>
)
