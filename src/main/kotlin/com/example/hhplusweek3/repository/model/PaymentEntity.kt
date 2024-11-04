package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.Payment
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "payments",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_payments_reservation_id",
            columnNames = [ "reservation_id"],
        ),
    ],
)
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val paymentId: String,
    val reservationId: String,
    val amount: Long,
    val createdTimeUtc: Instant,
) {
    fun toModel(): Payment =
        Payment(
            paymentId,
            amount,
            reservationId,
            createdTimeUtc,
        )

    constructor(payment: Payment) : this(
        0,
        payment.paymentId,
        payment.reservationId,
        payment.amount,
        payment.createdTimeUtc,
    )
}
