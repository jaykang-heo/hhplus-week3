package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import jakarta.transaction.Transactional
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
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

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 50, multiplier = 2.0, maxDelay = 1000),
    )
    fun <T> executeWithPessimisticLock(
        queueToken: String,
        action: () -> T,
    ): T {
        walletRepository.getOrCreateByQueueTokenWithPessimisticLockOrThrow(queueToken)
        return action.invoke()
    }

    fun <T> executeWithOptimisticLock(
        queueToken: String,
        action: () -> T,
    ): T {
        walletRepository.getOrCreateByQueueTokenWithOptimisticLockOrThrow(queueToken)
        return action.invoke()
    }

    fun createEmpty(queues: List<Queue>) {
        queues.forEach {
            add(0, it.token)
        }
    }
}
