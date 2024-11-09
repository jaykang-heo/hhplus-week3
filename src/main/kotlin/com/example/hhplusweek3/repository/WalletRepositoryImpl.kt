package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.repository.jpa.WalletEntityJpaRepository
import com.example.hhplusweek3.repository.model.WalletEntity
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class WalletRepositoryImpl(
    private val walletEntityJpaRepository: WalletEntityJpaRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
) : WalletRepository {
    private val hashOps: HashOperations<String, String, Any> = redisTemplate.opsForHash()
    private val expirationTime: Duration = Duration.ofDays(1)

    override fun save(wallet: Wallet): Wallet {
        val dataModel =
            walletEntityJpaRepository.findByQueueToken(wallet.queueToken)
                ?: WalletEntity(wallet)
        dataModel.balance = wallet.balance
        val savedWallet = walletEntityJpaRepository.save(dataModel).toModel()

        hashOps.put(CACHE_NAME, wallet.queueToken, savedWallet)
        redisTemplate.expire(CACHE_NAME, expirationTime)

        return savedWallet
    }

    override fun getByQueueToken(queueToken: String): Wallet =
        hashOps.get(CACHE_NAME, queueToken)?.let { it as Wallet }
            ?: walletEntityJpaRepository.findByQueueToken(queueToken)?.toModel()?.also { wallet ->
                hashOps.put(CACHE_NAME, queueToken, wallet)
                redisTemplate.expire(CACHE_NAME, expirationTime)
            }
            ?: throw IllegalStateException("Wallet not found for queue token: $queueToken")

    override fun findByQueueToken(queueToken: String): Wallet? =
        hashOps.get(CACHE_NAME, queueToken)?.let { it as Wallet }
            ?: walletEntityJpaRepository.findByQueueToken(queueToken)?.toModel()?.also { wallet ->
                hashOps.put(CACHE_NAME, queueToken, wallet)
                redisTemplate.expire(CACHE_NAME, expirationTime)
            }

    companion object {
        private const val CACHE_NAME = "wallets"
    }
}
