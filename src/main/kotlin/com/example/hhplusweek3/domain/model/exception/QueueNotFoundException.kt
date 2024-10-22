package com.example.hhplusweek3.domain.model.exception

class QueueNotFoundException(
    val queueToken: String,
    message: String = "Queue not found with token: $queueToken",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.QUEUE_NOT_FOUND, message, cause)
