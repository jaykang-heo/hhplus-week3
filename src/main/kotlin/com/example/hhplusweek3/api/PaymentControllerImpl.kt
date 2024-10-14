package com.example.hhplusweek3.api

import PayRequest
import com.example.hhplusweek3.api.contract.PaymentController
import com.example.hhplusweek3.api.response.PayResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class PaymentControllerImpl : PaymentController {
    override fun createPayment(request: PayRequest, authHeader: String): PayResponse {
        TODO("Not yet implemented")
    }
}
