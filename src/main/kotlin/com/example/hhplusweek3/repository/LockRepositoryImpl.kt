package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.port.LockRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.TimeUnit

@Repository
class LockRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redissonClient: RedissonClient,
) : LockRepository {
    override fun <T> spinLock(
        key: String,
        action: () -> T,
    ): T? {
        val timeout = 10L // Lock timeout in seconds
        val waitTime = 50L // Time between spin attempts in milliseconds
        val maxAttempts = 200 // Maximum number of attempts (10 seconds total)
        val lockToken = UUID.randomUUID().toString()
        val lockKey = "lock:$key"
        var attempts = 0

        while (attempts < maxAttempts) {
            try {
                val success =
                    redisTemplate
                        .opsForValue()
                        .setIfAbsent(lockKey, lockToken, timeout, TimeUnit.SECONDS)

                if (success == true) {
                    try {
                        return action()
                    } finally {
                        // Use Lua script for atomic delete operation
                        val script =
                            """
                            if redis.call('get', KEYS[1]) == ARGV[1] then
                                return redis.call('del', KEYS[1])
                            else
                                return 0
                            end
                            """.trimIndent()

                        redisTemplate.execute(
                            RedisScript.of(script, Long::class.java),
                            listOf(lockKey),
                            lockToken,
                        )
                    }
                }

                attempts++
                Thread.sleep(waitTime)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw e
            }
        }

        throw RuntimeException("Failed to acquire lock after $maxAttempts attempts")
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
