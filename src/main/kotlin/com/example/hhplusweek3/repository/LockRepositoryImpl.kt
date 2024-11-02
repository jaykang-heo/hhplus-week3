package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.port.LockRepository
import com.example.hhplusweek3.repository.redis.RedisRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class LockRepositoryImpl(
    private val redisRepository: RedisRepository,
) : LockRepository {
    override fun <T> acquirePaymentLock(
        queueToken: String,
        reservationId: String,
        action: () -> T,
    ): T? {
        val paymentLock = generatePaymentLocKey(queueToken, reservationId)
        return redisRepository.lock(paymentLock) {
            action.invoke()
        }
    }

    override fun <T> acquireWalletLock(
        queueToken: String,
        action: () -> T,
    ): T? {
        val walletLock = generateWalletLockKey(queueToken)
        return redisRepository.lock(walletLock) {
            action.invoke()
        }
    }

    override fun <T> acquireReservationLock(
        dateUtc: Instant,
        seatNumber: Long,
        action: () -> T,
    ): T? {
        val reservationLockKey = generateReservationLockKey(dateUtc, seatNumber)
        return redisRepository.lock(reservationLockKey) {
            action.invoke()
        }
    }

    companion object {
        fun generatePaymentLocKey(
            queueToken: String,
            reservationId: String,
        ): String = "distributionLock::payment::$queueToken::$reservationId"

        fun generateWalletLockKey(queueToken: String): String = "distributionLock::wallet::$queueToken"

        fun generateReservationLockKey(
            dateUtc: Instant,
            seatNumber: Long,
        ): String = "distributionLock::wallet::$dateUtc::$seatNumber"
    }
}
