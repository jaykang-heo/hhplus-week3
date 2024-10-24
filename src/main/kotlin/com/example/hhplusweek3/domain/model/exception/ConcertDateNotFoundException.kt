package com.example.hhplusweek3.domain.model.exception

import java.time.Instant

class ConcertDateNotFoundException(
    val date: Instant,
    message: String = "No concert exists for date: $date",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.CONCERT_DATE_NOT_FOUND, message, cause)
