package com.example.hhplusweek3.domain.model.exception

class InsufficientBalanceException(
    val currentBalance: Long,
    val requiredAmount: Long,
    message: String = "Insufficient balance. Current: $currentBalance, Required: $requiredAmount",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.INSUFFICIENT_BALANCE, message, cause)
