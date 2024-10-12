package com.example.hhplusweek3.api.response

import java.time.Instant

data class GetQueueInfoResponse(
    val userId: String,
    val currentCount: Long,
    val totalCount: Long,
    val createdAt: Instant
)
