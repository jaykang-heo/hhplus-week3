package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.contract.WalletController
import com.example.hhplusweek3.api.request.ChargeWalletBalanceRequest
import com.example.hhplusweek3.api.response.ChargeWalletBalanceResponse
import com.example.hhplusweek3.api.response.GetWalletBalanceResponse
import com.example.hhplusweek3.application.WalletFacade
import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/wallets")
class WalletControllerImpl(
    private val walletFacade: WalletFacade
) : WalletController {

    override fun chargeWalletBalance(
        request: ChargeWalletBalanceRequest,
        authHeader: String
    ): ChargeWalletBalanceResponse {
        val command = ChargeWalletCommand(request, authHeader)
        val wallet = walletFacade.charge(command)
        return ChargeWalletBalanceResponse(wallet)
    }

    override fun getWalletBalance(authHeader: String): GetWalletBalanceResponse {
        val query = GetWalletBalanceQuery(authHeader)
        val wallet = walletFacade.get(query)
        return GetWalletBalanceResponse(wallet.balance)
    }
}
