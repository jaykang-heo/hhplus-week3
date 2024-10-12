package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class QueueRepositoryImpl(
    private val queueEntityJpaRepository: QueueEntityJpaRepository
) : QueueRepository {
    override fun save(queue: Queue): Queue {
        val entity = QueueEntity(queue)
        return queueEntityJpaRepository.save(entity).toModel()
    }

    override fun findAllByActiveAndBeforeTime(time: Instant): List<Queue> {
        return queueEntityJpaRepository
            .findAllByStatusAndExpirationTimeUtcBefore(QueueStatus.ACTIVE, time)
            .map { it.toModel() }
    }

    @Transactional
    override fun changeStatusToExpire(tokens: List<String>) {
        val expiredQueues = queueEntityJpaRepository
            .findAllByTokenIn(tokens.toSet())
            .map {
                it.status = QueueStatus.EXPIRED
                it
            }
        queueEntityJpaRepository.saveAll(expiredQueues)
    }
}
