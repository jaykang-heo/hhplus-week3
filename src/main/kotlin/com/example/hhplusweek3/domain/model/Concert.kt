package com.example.hhplusweek3.domain.model

import java.time.Instant

data class Concert(
    val availableSchedules: List<Schedule>,
    val allSchedules: List<Schedule>

) {
    data class Schedule(
        val date: Instant,
        val seats: List<Seat>
    )

    data class Seat(
        val number: Long
    )
}
