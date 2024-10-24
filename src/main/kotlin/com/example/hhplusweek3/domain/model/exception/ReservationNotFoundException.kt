package com.example.hhplusweek3.domain.model.exception

class ReservationNotFoundException(
    val queueToken: String,
    val reservationId: String,
    message: String = "Reservation not found with token: $queueToken and reservationId: $reservationId",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.RESERVATION_NOT_FOUND, message, cause)
