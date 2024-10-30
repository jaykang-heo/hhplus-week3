package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.port.RedisRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.TimeUnit

@Repository
class RedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
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
}
