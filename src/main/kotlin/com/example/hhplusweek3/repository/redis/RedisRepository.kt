package com.example.hhplusweek3.repository.redis

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisRepository(
    private val redissonClient: RedissonClient,
) {
    fun <T> lock(
        lockKey: String,
        action: () -> T,
    ): T? {
        val lock: RLock = redissonClient.getLock(lockKey)

        return try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                action.invoke()
            } else {
                null
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            null
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
