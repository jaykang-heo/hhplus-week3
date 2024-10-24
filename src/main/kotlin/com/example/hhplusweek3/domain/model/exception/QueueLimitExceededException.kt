package com.example.hhplusweek3.domain.model.exception

class QueueLimitExceededException(
    private val currentCount: Long,
    private val limitCount: Long,
    message: String = "Queue issued count ($currentCount) exceeded the limit ($limitCount)",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.QUEUE_LIMIT_EXCEEDED, message, cause)
