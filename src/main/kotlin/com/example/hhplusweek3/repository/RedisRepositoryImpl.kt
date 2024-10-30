package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.port.RedisRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.TimeUnit

@Repository
class RedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redissonClient: RedissonClient,
) : RedisRepository {
    override fun <T> spinLock(
        key: String,
        action: () -> T,
    ): T? {
        val timeout = 10L
        val waitTime = 100L
        val lockToken = UUID.randomUUID().toString()
        val lockKey = "lock:$key"

        try {
            while (true) {
                val success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockToken, timeout, TimeUnit.SECONDS)
                if (success == true) {
                    try {
                        return action()
                    } finally {
                        val currentToken = redisTemplate.opsForValue().get(lockKey) as? String
                        if (lockToken == currentToken) {
                            redisTemplate.delete(lockKey)
                        }
                    }
                }
                try {
                    Thread.sleep(waitTime)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw e
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun <T> redLock(
        key: String,
        action: () -> T,
    ): T? {
        val waitTime = 1000L
        val leaseTime = 10000L
        val lock: RLock = redissonClient.getLock(key)
        try {
            val acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)
            if (acquired) {
                try {
                    return action()
                } finally {
                    if (lock.isHeldByCurrentThread) {
                        lock.unlock()
                    }
                }
            } else {
                throw RuntimeException("Could not acquire red lock for key: $key")
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Thread was interrupted while trying to acquire red lock for key: $key", e)
        }
    }
}
