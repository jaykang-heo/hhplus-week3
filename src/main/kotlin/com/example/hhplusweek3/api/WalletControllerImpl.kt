package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.contract.WalletController
import com.example.hhplusweek3.api.request.ChargeWalletBalanceRequest
import com.example.hhplusweek3.api.response.ChargeWalletBalanceResponse
import com.example.hhplusweek3.api.response.GetWalletBalanceResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/wallets")
class WalletControllerImpl : WalletController {
    override fun chargeWalletBalance(
        request: ChargeWalletBalanceRequest,
        authHeader: String
    ): ChargeWalletBalanceResponse {
        TODO("Not yet implemented")
    }

    override fun getWalletBalance(authHeader: String): GetWalletBalanceResponse {
        TODO("Not yet implemented")
    }
}
