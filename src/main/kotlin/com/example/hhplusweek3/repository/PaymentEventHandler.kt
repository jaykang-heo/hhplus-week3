package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.PaymentCreatedEvent
import com.example.hhplusweek3.repository.jpa.PaymentAuditJpaRepository
import com.example.hhplusweek3.repository.model.PaymentAuditEntity
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PaymentEventHandler(
    private val paymentAuditJpaRepository: PaymentAuditJpaRepository,
) {
    @EventListener
    fun handlePaymentCreatedEvent(event: PaymentCreatedEvent) {
        val paymentAuditEntity =
            PaymentAuditEntity(
                paymentId = event.payment.paymentId,
                reservationId = event.payment.reservationId,
                amount = event.payment.amount,
                createdAt = Instant.now(),
            )
        paymentAuditJpaRepository.save(paymentAuditEntity)
    }
}
