package com.example.hhplusweek3.domain.model.exception

import com.example.hhplusweek3.domain.model.QueueStatus

class InvalidQueueStatusException(
    private val currentStatus: QueueStatus,
    message: String = "Queue is not active. Current status: $currentStatus",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.INVALID_QUEUE_STATUS, message, cause)
