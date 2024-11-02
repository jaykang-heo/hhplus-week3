package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.model.exception.WalletNotFoundException
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.repository.jpa.WalletEntityJpaRepository
import com.example.hhplusweek3.repository.model.WalletEntity
import org.springframework.stereotype.Repository

@Repository
class WalletRepositoryImpl(
    private val walletEntityJpaRepository: WalletEntityJpaRepository,
) : WalletRepository {
    override fun save(wallet: Wallet): Wallet {
        val dataModel =
            walletEntityJpaRepository.findByQueueToken(wallet.queueToken)
                ?: WalletEntity(wallet)
        dataModel.balance = wallet.balance
        return walletEntityJpaRepository.save(dataModel).toModel()
    }

    override fun getByQueueToken(queueToken: String): Wallet = walletEntityJpaRepository.findByQueueToken(queueToken)!!.toModel()

    override fun findByQueueToken(queueToken: String): Wallet? = walletEntityJpaRepository.findByQueueToken(queueToken)?.toModel()

    override fun getOrCreateByQueueTokenWithPessimisticLockOrThrow(queueToken: String): Wallet =
        walletEntityJpaRepository.findByQueueTokenWithPessimisticLock(queueToken)?.toModel()
            ?: throw WalletNotFoundException(queueToken)

    override fun getOrCreateByQueueTokenWithOptimisticLockOrThrow(queueToken: String): Wallet =
        walletEntityJpaRepository.findByQueueTokenWithOptimisticLock(queueToken)?.toModel()
            ?: throw WalletNotFoundException(queueToken)
}
