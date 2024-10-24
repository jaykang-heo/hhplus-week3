package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.repository.jpa.PaymentEntityJpaRepository
import com.example.hhplusweek3.repository.model.PaymentEntity
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
    private val paymentEntityJpaRepository: PaymentEntityJpaRepository,
) : PaymentRepository {
    override fun save(
        payment: Payment,
        queueToken: String,
    ): Payment {
        val dataModel = PaymentEntity(payment, queueToken)
        return paymentEntityJpaRepository.save(dataModel).toModel()
    }

    override fun findByQueueTokenAndReservationId(
        queueToken: String,
        reservationId: String,
    ): Payment? = paymentEntityJpaRepository.findByQueueTokenAndReservationId(queueToken, reservationId)?.toModel()
}
