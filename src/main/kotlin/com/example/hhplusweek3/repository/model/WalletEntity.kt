package com.example.hhplusweek3.repository.model

import com.example.hhplusweek3.domain.model.Wallet
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "wallets")
data class WalletEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val queueToken: String,
    var balance: Long,
    val createdTimeUtc: Instant,
    val updatedTimeUtc: Instant,
) {
    fun toModel(): Wallet =
        Wallet(
            balance,
            queueToken,
        )

    constructor(wallet: Wallet) : this(
        0,
        wallet.queueToken,
        wallet.balance,
        Instant.now(),
        Instant.now(),
    )
}
