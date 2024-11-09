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
    override fun save(payment: Payment): Payment =
        paymentEntityJpaRepository
            .save(PaymentEntity(payment))
            .toModel()

    override fun findByReservationId(reservationId: String): Payment? =
        paymentEntityJpaRepository
            .findByReservationId(reservationId)
            ?.toModel()
}
