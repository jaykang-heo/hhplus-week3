package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import com.example.hhplusweek3.domain.service.WalletService
import com.example.hhplusweek3.domain.validator.ChargeWalletCommandValidator
import com.example.hhplusweek3.domain.validator.GetWalletBalanceQueryValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WalletFacade(
    private val walletService: WalletService,
    private val chargeWalletCommandValidator: ChargeWalletCommandValidator,
    private val getWalletBalanceQueryValidator: GetWalletBalanceQueryValidator,
    private val walletRepository: WalletRepository,
) {
    @Transactional
    fun charge(command: ChargeWalletCommand): Wallet {
        walletService.executeWithLock(command.queueToken) {
            chargeWalletCommandValidator.validate(command)
            walletService.add(command.amount, command.queueToken)
        }
        return walletRepository.getByQueueToken(command.queueToken)
    }

    fun get(query: GetWalletBalanceQuery): Wallet {
        getWalletBalanceQueryValidator.validate(query)
        return walletRepository.findByQueueToken(query.queueToken) ?: Wallet(0, query.queueToken)
    }
}
