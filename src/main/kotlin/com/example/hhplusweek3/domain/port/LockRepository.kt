package com.example.hhplusweek3.domain.port

import java.time.Instant

interface LockRepository {
    fun <T> acquirePaymentLock(
        queueToken: String,
        reservationId: String,
        action: () -> T,
    ): T?

    fun <T> acquireWalletLock(
        queueToken: String,
        action: () -> T,
    ): T?

    fun <T> acquireReservationLock(
        dateUtc: Instant,
        seatNumber: Long,
        action: () -> T,
    ): T?
}
