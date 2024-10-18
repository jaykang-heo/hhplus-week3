package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.Payment
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val paymentId: String,
    val reservationId: String,
    val queueToken: String,
    val amount: Long,
    val createdTimeUtc: Instant
) {
    fun toModel(): Payment {
        return Payment(
            paymentId,
            amount,
            reservationId,
            createdTimeUtc
        )
    }

    constructor(payment: Payment, queueToken: String) : this(
        0,
        payment.paymentId,
        payment.reservationId,
        queueToken,
        payment.amount,
        payment.createdTimeUtc
    )
}
