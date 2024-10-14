package com.example.hhplusweek3.domain.command

data class CreatePaymentCommand(
    val queueToken: String,
    val reservationId: String
) {
    fun validate() {
        require(queueToken.isNotBlank()) { "Queue token is blank" }
        require(reservationId.isNotBlank()) { "Reservation id is blank" }
    }
}
