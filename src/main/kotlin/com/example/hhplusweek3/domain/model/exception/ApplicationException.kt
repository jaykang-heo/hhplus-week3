package com.example.hhplusweek3.domain.model.exception

abstract class ApplicationException(
    val errorCode: ErrorCode,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
