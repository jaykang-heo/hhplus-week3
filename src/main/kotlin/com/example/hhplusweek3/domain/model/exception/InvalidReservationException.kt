package com.example.hhplusweek3.domain.model.exception

import java.time.Instant

class InvalidReservationException(
    val date: Instant,
    val seatNumber: Long,
    val token: String,
    message: String = "Cannot make reservation for date: $date and seat number: $seatNumber",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.INVALID_RESERVATION, message, cause)
