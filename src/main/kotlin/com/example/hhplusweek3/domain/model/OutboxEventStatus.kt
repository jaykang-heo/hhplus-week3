package com.example.hhplusweek3.domain.model

enum class OutboxEventStatus {
    PENDING,
    PROCESSED,
    FAILED,
}
