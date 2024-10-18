package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WalletEntityJpaRepository : JpaRepository<WalletEntity, Long> {
    fun findByQueueToken(queueToken: String): WalletEntity?
}
