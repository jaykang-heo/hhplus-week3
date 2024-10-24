package com.example.hhplusweek3.domain.model.exception

class AlreadyPaidException(
    val queueToken: String,
    val reservationId: String,
    message: String = "Payment already made for $queueToken. reservation: $reservationId",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.ALREADY_PAID_RESERVATION, message, cause)
