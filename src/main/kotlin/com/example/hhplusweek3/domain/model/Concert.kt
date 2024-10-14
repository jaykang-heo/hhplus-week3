package com.example.hhplusweek3.domain.model

data class Concert(
    val availableSeats: List<ConcertSeat>,
    val allSeats: List<ConcertSeat>
)
