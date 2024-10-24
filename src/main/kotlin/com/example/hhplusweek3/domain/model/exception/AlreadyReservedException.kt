package com.example.hhplusweek3.domain.model.exception

import java.time.Instant

class AlreadyReservedException(
    val date: Instant,
    val seatNumber: Long,
    message: String = "Already reserved for date: $date and seat number: $seatNumber",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.INVALID_RESERVATION, message, cause)
