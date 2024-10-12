package com.example.hhplusweek3.domain.model

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import java.time.Instant
import java.util.UUID

data class Queue(
    val token: String,
    val status: QueueStatus,
    val expirationTimeUtc: Instant,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant
) {
    constructor(command: IssueQueueTokenCommand) : this(
        generateQueueToken(),
        QueueStatus.PENDING,
        generateQueueTokenExpirationTime(),
        Instant.now(),
        Instant.now()
    )

    companion object {
        fun generateQueueToken(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }

        fun generateQueueTokenExpirationTime(): Instant {
            return Instant.now().plusSeconds(EXPIRATION_INTERVAL_SECONDS)
        }
        private const val EXPIRATION_INTERVAL_SECONDS: Long = 60
    }
}
