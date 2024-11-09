package com.example.hhplusweek3.domain.model

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import java.io.Serializable
import java.time.Instant
import java.util.UUID

data class Payment(
    val paymentId: String,
    val amount: Long,
    val reservationId: String,
    val createdTimeUtc: Instant,
) : Serializable {
    constructor(command: CreatePaymentCommand, amount: Long) : this(
        UUID.randomUUID().toString(),
        amount,
        command.reservationId,
        Instant.now(),
    )
}
