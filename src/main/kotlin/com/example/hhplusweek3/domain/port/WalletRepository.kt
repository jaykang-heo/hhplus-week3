package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Wallet

interface WalletRepository {
    fun save(wallet: Wallet): Wallet

    fun getByQueueToken(queueToken: String): Wallet

    fun findByQueueToken(queueToken: String): Wallet?

    fun getOrCreateByQueueTokenWithLockOrThrow(queueToken: String): Wallet
}
