package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.Queue
import java.time.Instant

interface QueueRepository {
    fun save(queue: Queue): Queue
    fun findAllByActiveAndBeforeTime(time: Instant): List<Queue>
    fun changeStatusToExpire(tokens: List<String>)
}
