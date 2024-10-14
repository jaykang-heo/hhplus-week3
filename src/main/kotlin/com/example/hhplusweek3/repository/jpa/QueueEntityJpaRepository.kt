package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.repository.model.QueueEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface QueueEntityJpaRepository : JpaRepository<QueueEntity, Long> {
    fun countAllByStatus(status: QueueStatus): Long
    fun findAllByStatusAndExpirationTimeUtcBefore(status: QueueStatus, expirationTime: Instant): List<QueueEntity>
    fun findAllByTokenIn(tokens: Set<String>): List<QueueEntity>
    fun findByToken(token: String): QueueEntity?
    fun findAllByStatus(status: QueueStatus): List<QueueEntity>
}
