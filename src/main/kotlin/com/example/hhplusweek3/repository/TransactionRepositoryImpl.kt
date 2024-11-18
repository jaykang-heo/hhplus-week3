package com.example.hhplusweek3.repository

import com.example.hhplusweek3.domain.port.TransactionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class TransactionRepositoryImpl : TransactionRepository {
    @Transactional
    override fun <T> transactional(action: () -> T): T = action.invoke()
}
