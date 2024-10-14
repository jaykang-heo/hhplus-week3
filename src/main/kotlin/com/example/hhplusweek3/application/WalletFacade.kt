package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.validator.ChargeWalletCommandValidator
import org.springframework.stereotype.Service

@Service
class WalletFacade(
    private val walletRepository: WalletRepository,
    private val queueService: QueueService,
    private val chargeWalletCommandValidator: ChargeWalletCommandValidator
) {

    fun charge(command: ChargeWalletCommand): Wallet {
        queueService.preRun(command.token)
        chargeWalletCommandValidator.validate(command)
        return walletRepository.charge(command.amount, command.token)
    }
}
