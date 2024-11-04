package com.example.hhplusweek3.domain.model

import java.io.Serializable

data class Wallet(
    var balance: Long,
    val queueToken: String,
) : Serializable
