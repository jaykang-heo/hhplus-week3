package com.example.hhplusweek3.domain.port

import com.example.hhplusweek3.domain.model.QueueLimit

interface QueueLimitRepository {
    fun getQueueLimit(): QueueLimit
}
