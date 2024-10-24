package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Payment

interface PaymentRepository {
    fun save(
        payment: Payment,
        queueToken: String,
    ): Payment

    fun findByQueueTokenAndReservationId(
        queueToken: String,
        reservationId: String,
    ): Payment?
}
