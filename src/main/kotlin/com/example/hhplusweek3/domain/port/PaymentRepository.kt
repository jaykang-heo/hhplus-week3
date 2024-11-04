package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Payment

interface PaymentRepository {
    fun save(payment: Payment): Payment

    fun findByReservationId(reservationId: String): Payment?
}
