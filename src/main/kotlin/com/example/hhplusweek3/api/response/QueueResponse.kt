package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Queue
import java.time.Instant

data class QueueResponse(
    val token: String,
    val expirationTimeUtc: Instant,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant
) {
    constructor(queue: Queue) : this(
        token = queue.token,
        expirationTimeUtc = queue.expirationTimeUtc,
        createdTimeUtc = queue.createdTimeUtc,
        updatedTimeUtc = queue.updatedTimeUtc
    )
}
