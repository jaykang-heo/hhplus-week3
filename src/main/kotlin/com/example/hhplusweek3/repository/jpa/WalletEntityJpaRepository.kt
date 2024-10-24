package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.WalletEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WalletEntityJpaRepository : JpaRepository<WalletEntity, Long> {
    fun findByQueueToken(queueToken: String): WalletEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WalletEntity w  where w.queueToken = :queueToken")
    fun findByQueueTokenWithLock(
        @Param("queueToken") queueToken: String,
    ): WalletEntity?
}
