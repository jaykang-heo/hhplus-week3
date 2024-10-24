package com.example.hhplusweek3.domain.model.exception

import java.time.Instant

class ConcertSeatNotFoundException(
    val date: Instant,
    val seatNumber: Long,
    message: String = "Concert seat not found for date: $date and seat number: $seatNumber",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.CONCERT_SEAT_NOT_FOUND, message, cause)
