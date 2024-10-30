package com.example.hhplusweek3.domain.port

interface RedisRepository {
    fun <T> spinLock(
        key: String,
        action: () -> T,
    ): T?

    fun <T> redLock(
        key: String,
        action: () -> T,
    ): T?
}
