package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.request.ChargeBalanceRequest
import com.example.hhplusweek3.api.request.PayRequest
import com.example.hhplusweek3.api.response.ChargeBalanceResponse
import com.example.hhplusweek3.api.response.GetBalanceResponse
import com.example.hhplusweek3.api.response.PayResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import kotlin.random.Random

@RestController
@RequestMapping("/payment")
class PaymentController {

    @PostMapping("/pay")
    fun pay(
        @RequestBody request: PayRequest,
        @RequestHeader("Authorization") authHeader: String
    ): PayResponse {
        return PayResponse(
            UUID.randomUUID().toString(),
            Random.nextLong(1, Long.MAX_VALUE)
        )
    }

    @PostMapping("/charge")
    fun chargeBalance(
        @RequestBody request: ChargeBalanceRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ChargeBalanceResponse {
        return ChargeBalanceResponse(
            UUID.randomUUID().toString(),
            Random.nextLong(1, Long.MAX_VALUE)
        )
    }

    @GetMapping("/balance")
    fun getBalance(
        @RequestHeader("Authorization") authHeader: String
    ): GetBalanceResponse {
        return GetBalanceResponse(
            UUID.randomUUID().toString(),
            Random.nextLong(1, Long.MAX_VALUE)
        )
    }
}
