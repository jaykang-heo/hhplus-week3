package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.QueueLimit
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueLimitRepository
import org.springframework.stereotype.Repository

@Repository
class QueueLimitRepositoryImpl(
    private val queueEntityJpaRepository: QueueEntityJpaRepository
) : QueueLimitRepository {

    override fun getQueueLimit(): QueueLimit {
        val activeCounts = queueEntityJpaRepository.countAllByStatus(QueueStatus.ACTIVE)
        return QueueLimit(activeCounts, QUEUE_LIMIT_COUNT)
    }

    companion object {
        private const val QUEUE_LIMIT_COUNT: Long = 100
    }
}
