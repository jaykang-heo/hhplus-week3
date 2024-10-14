package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Wallet

interface WalletRepository {
    fun charge(amount: Long, queueToken: String): Wallet
    fun getByQueueToken(queueToken: String): Wallet
    fun findByQueueToken(queueToken: String): Wallet?
}
