package com.example.hhplusweek3.domain.port

interface LockRepository {
    fun <T> spinLock(
        key: String,
        action: () -> T,
    ): T?

    fun <T> redLock(
        key: String,
        action: () -> T,
    ): T?
}
