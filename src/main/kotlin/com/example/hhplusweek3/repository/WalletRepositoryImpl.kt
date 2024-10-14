package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.repository.jpa.WalletEntityJpaRepository
import com.example.hhplusweek3.repository.model.WalletEntity
import org.springframework.stereotype.Repository

@Repository
class WalletRepositoryImpl(
    private val walletEntityJpaRepository: WalletEntityJpaRepository
) : WalletRepository {

    override fun charge(amount: Long, queueToken: String): Wallet {
        val dataModel = walletEntityJpaRepository.findByQueueToken(queueToken)
            ?: WalletEntity(amount, queueToken)
        dataModel.balance += amount
        return walletEntityJpaRepository.save(dataModel).toModel()
    }

    override fun getByQueueToken(queueToken: String): Wallet {
        return walletEntityJpaRepository.findByQueueToken(queueToken)!!.toModel()
    }

    override fun findByQueueToken(queueToken: String): Wallet? {
        return walletEntityJpaRepository.findByQueueToken(queueToken)?.toModel()
    }
}
