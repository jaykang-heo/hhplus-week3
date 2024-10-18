package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Queue
import java.time.Instant

interface QueueRepository {
    fun save(queue: Queue): Queue
    fun update(queue: Queue): Queue
    fun findAllByActiveAndBeforeTime(time: Instant): List<Queue>
    fun changeStatusToExpire(tokens: List<String>)
    fun getByToken(token: String): Queue
    fun findByToken(token: String): Queue?
    fun findAllPending(): List<Queue>
    fun findAllActive(): List<Queue>
    fun changeStatusToActive(token: String)
}
