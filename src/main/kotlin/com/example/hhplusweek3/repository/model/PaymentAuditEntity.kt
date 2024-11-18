package com.example.hhplusweek3.repository.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "payment_audit")
class PaymentAuditEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val paymentId: String,
    val reservationId: String,
    val amount: Long,
    val createdAt: Instant,
)
