package com.example.hhplusweek3.domain.port

interface TransactionRepository {
    fun <T> transactional(action: () -> T): T
}
