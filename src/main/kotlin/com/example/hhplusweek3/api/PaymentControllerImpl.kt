package com.example.hhplusweek3.api

import CreatePaymentRequest
import com.example.hhplusweek3.api.contract.PaymentController
import com.example.hhplusweek3.api.response.CreatePaymentResponse
import com.example.hhplusweek3.application.PaymentFacade
import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentControllerImpl(
    private val paymentFacade: PaymentFacade
) : PaymentController {

    override fun createPayment(request: CreatePaymentRequest, authHeader: String): CreatePaymentResponse {
        val command = CreatePaymentCommand(authHeader, request.reservationId)
        val payment = paymentFacade.createPayment(command)
        return CreatePaymentResponse(payment.paymentId)
    }
}
