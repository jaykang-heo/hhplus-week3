package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import org.springframework.stereotype.Component

@Component
class WalletService(
    private val walletRepository: WalletRepository,
) {
    fun add(
        amount: Long,
        queueToken: String,
    ) {
        val wallet =
            walletRepository.findByQueueToken(queueToken)
                ?: Wallet(0, queueToken)
        wallet.balance += amount
        walletRepository.save(wallet)
    }

    fun redeem(
        amount: Long,
        queueToken: String,
    ) {
        val wallet = walletRepository.getByQueueToken(queueToken)
        wallet.balance -= amount
        walletRepository.save(wallet)
    }

    fun executeWithLock(
        queueToken: String,
        action: () -> Unit,
    ) {
        walletRepository.getOrCreateByQueueTokenWithLockOrThrow(queueToken)
        action.invoke()
    }

    fun createEmpty(queues: List<Queue>) {
        queues.forEach {
            add(0, it.token)
        }
    }
}
