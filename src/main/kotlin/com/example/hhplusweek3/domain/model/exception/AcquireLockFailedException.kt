package com.example.hhplusweek3.domain.model.exception

class AcquireLockFailedException(
    val type: String,
    message: String = "Acquire lock failed",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.ACQUIRE_LOCK_FAILED, message, cause)
