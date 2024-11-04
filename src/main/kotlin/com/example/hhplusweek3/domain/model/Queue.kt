package com.example.hhplusweek3.domain.model

import java.time.Instant
import java.util.UUID

data class Queue(
    val token: String,
    val status: QueueStatus,
    var expirationTimeUtc: Instant,
    val createdTimeUtc: Instant,
    var updatedTimeUtc: Instant,
) {
    constructor() : this(
        generateQueueToken(),
        QueueStatus.PENDING,
        generateQueueTokenExpirationTime(),
        Instant.now(),
        Instant.now(),
    )

    fun extendExpirationTime(time: Instant) {
        expirationTimeUtc = time.plusSeconds(EXPIRATION_INTERVAL_SECONDS)
    }

    fun updateUpdatedTime(time: Instant) {
        updatedTimeUtc = time
    }

    companion object {
        fun generateQueueToken(): String = UUID.randomUUID().toString().replace("-", "")

        fun generateQueueTokenExpirationTime(): Instant = Instant.now().plusSeconds(EXPIRATION_INTERVAL_SECONDS)

        private const val EXPIRATION_INTERVAL_SECONDS: Long = 60
    }
}
