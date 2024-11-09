package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class QueueRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>,
) : QueueRepository {
    companion object {
        const val QUEUE_PREFIX = "queue:"
        const val PENDING_ZSET = "queues:pending"
        const val ACTIVE_SET = "queues:active"
    }

    override fun save(queue: Queue): Queue {
        val key = QUEUE_PREFIX + queue.token
        val queueData =
            mapOf(
                "token" to queue.token,
                "status" to queue.status.name,
                "createdTimeUtc" to queue.createdTimeUtc.toString(),
                "expirationTimeUtc" to queue.expirationTimeUtc.toString(),
            )
        redisTemplate.opsForHash<String, String>().putAll(key, queueData)
        val score = queue.createdTimeUtc.toEpochMilli().toDouble()
        redisTemplate.opsForZSet().add(PENDING_ZSET, queue.token, score)
        return queue
    }

    override fun getByToken(token: String): Queue = findByToken(token) ?: throw Exception("Queue not found")

    override fun findByToken(token: String): Queue? {
        val key = QUEUE_PREFIX + token
        val queueData = redisTemplate.opsForHash<String, String>().entries(key)
        if (queueData.isEmpty()) return null
        return Queue.fromMap(queueData)
    }

    override fun expireBeforeTime(time: Instant) {
        val tokens = redisTemplate.opsForSet().members(ACTIVE_SET) ?: emptySet()
        tokens.forEach { token ->
            val key = QUEUE_PREFIX + token
            val expirationTimeStr = redisTemplate.opsForHash<String, String>().get(key, "expirationTimeUtc")
            if (expirationTimeStr != null) {
                val expirationTime = Instant.parse(expirationTimeStr)
                if (expirationTime.isBefore(time)) {
                    redisTemplate.opsForHash<String, String>().put(key, "status", QueueStatus.EXPIRED.name)
                    redisTemplate.opsForSet().remove(ACTIVE_SET, token)
                }
            }
        }
    }

    override fun countActiveQueues(): Int = redisTemplate.opsForSet().size(ACTIVE_SET)?.toInt() ?: 0

    override fun findPendingTokens(limit: Long): Set<String> =
        redisTemplate
            .opsForZSet()
            .range(PENDING_ZSET, 0, limit - 1) ?: emptySet()

    override fun activatePendingQueues(tokens: Set<String>): List<Queue> {
        if (tokens.isEmpty()) return emptyList()

        tokens.forEach { token ->
            redisTemplate.opsForZSet().remove(PENDING_ZSET, token)
            redisTemplate.opsForSet().add(ACTIVE_SET, token)
            val key = QUEUE_PREFIX + token
            redisTemplate.opsForHash<String, String>().put(key, "status", QueueStatus.ACTIVE.name)
        }

        return tokens.mapNotNull { findByToken(it) }
    }
}
