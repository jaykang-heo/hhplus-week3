package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "queues")
class QueueEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    val token: String,
    var status: QueueStatus,
    val expirationTimeUtc: Instant,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant
) {
    fun toModel(): Queue {
        return Queue(
            token,
            status,
            expirationTimeUtc,
            createdTimeUtc,
            updatedTimeUtc
        )
    }

    constructor(queue: Queue) : this(
        0,
        queue.token,
        queue.status,
        queue.expirationTimeUtc,
        queue.createdTimeUtc,
        queue.updatedTimeUtc
    )
}
