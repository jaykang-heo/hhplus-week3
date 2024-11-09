package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Queue
import java.time.Instant

interface QueueRepository {
    fun save(queue: Queue): Queue

    fun getByToken(token: String): Queue

    fun findByToken(token: String): Queue?

    fun expireBeforeTime(time: Instant)

    fun countActiveQueues(): Int

    fun findPendingTokens(limit: Long): Set<String>

    fun activatePendingQueues(tokens: Set<String>): List<Queue>
}
