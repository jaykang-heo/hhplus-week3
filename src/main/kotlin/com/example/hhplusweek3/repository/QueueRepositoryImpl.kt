package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.repository.jpa.QueueEntityJpaRepository
import com.example.hhplusweek3.repository.model.QueueEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class QueueRepositoryImpl(
    private val queueEntityJpaRepository: QueueEntityJpaRepository,
) : QueueRepository {
    override fun save(queue: Queue): Queue {
        val entity = QueueEntity(queue)
        return queueEntityJpaRepository.save(entity).toModel()
    }

    override fun update(queue: Queue): Queue {
        val entity = QueueEntity(queue)
        entity.id = queueEntityJpaRepository.findByToken(entity.token)!!.id
        return queueEntityJpaRepository.save(entity).toModel()
    }

    override fun findAllByActiveAndBeforeTime(time: Instant): List<Queue> =
        queueEntityJpaRepository
            .findAllByStatusAndExpirationTimeUtcBefore(QueueStatus.ACTIVE, time)
            .map { it.toModel() }

    @Transactional
    override fun changeStatusToExpire(tokens: List<String>) {
        val expiredQueues =
            queueEntityJpaRepository
                .findAllByTokenIn(tokens.toSet())
                .map {
                    it.status = QueueStatus.EXPIRED
                    it
                }
        queueEntityJpaRepository.saveAll(expiredQueues)
    }

    override fun getByToken(token: String): Queue = queueEntityJpaRepository.findByToken(token)!!.toModel()

    override fun findByToken(token: String): Queue? = queueEntityJpaRepository.findByToken(token)?.toModel()

    override fun findAllPending(): List<Queue> = queueEntityJpaRepository.findAllByStatus(QueueStatus.PENDING).map { it.toModel() }

    override fun findAllActive(): List<Queue> = queueEntityJpaRepository.findAllByStatus(QueueStatus.ACTIVE).map { it.toModel() }

    override fun changeStatusToActive(token: String): Queue {
        val dataModel = queueEntityJpaRepository.findByToken(token)!!
        dataModel.status = QueueStatus.ACTIVE
        return queueEntityJpaRepository.save(dataModel).toModel()
    }
}
