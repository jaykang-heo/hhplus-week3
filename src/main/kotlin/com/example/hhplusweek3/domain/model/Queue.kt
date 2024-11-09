package com.example.hhplusweek3.domain.model

import java.time.Instant
import java.util.UUID

data class Queue(
    val token: String,
    var status: QueueStatus,
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

    companion object {
        fun generateQueueToken(): String = UUID.randomUUID().toString().replace("-", "")

        fun generateQueueTokenExpirationTime(): Instant = Instant.now().plusSeconds(EXPIRATION_INTERVAL_SECONDS)

        fun fromMap(map: Map<String, String>): Queue =
            Queue(
                token = map["token"] ?: throw IllegalArgumentException("Token is missing"),
                status = QueueStatus.valueOf(map["status"] ?: "PENDING"),
                expirationTimeUtc = Instant.parse(map["expirationTimeUtc"] ?: Instant.now().toString()),
                createdTimeUtc = Instant.parse(map["createdTimeUtc"] ?: Instant.now().toString()),
                updatedTimeUtc = Instant.parse(map["updatedTimeUtc"] ?: Instant.now().toString()),
            )

        const val EXPIRATION_INTERVAL_SECONDS: Long = 60
    }
}
