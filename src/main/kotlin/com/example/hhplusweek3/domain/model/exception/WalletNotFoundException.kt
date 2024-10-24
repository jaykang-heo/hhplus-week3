package com.example.hhplusweek3.domain.model.exception

class WalletNotFoundException(
    val queueToken: String,
    message: String = "Wallet not found for queue token: $queueToken",
    cause: Throwable? = null,
) : ApplicationException(ErrorCode.WALLET_NOT_FOUND, message, cause)
