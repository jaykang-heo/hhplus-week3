package com.example.hhplusweek3.domain.model

import java.time.Instant

data class Concert(
    val availableSeats: List<ConcertSeat>,
    val allSeats: List<ConcertSeat>,
    val availableDates: List<Instant>,
    val allDates: List<Instant>
)
