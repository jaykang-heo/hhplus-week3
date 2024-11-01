package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.port.WalletRepository
import jakarta.transaction.Transactional
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class PaymentService(
    private val reservationRepository: ReservationRepository,
    private val walletRepository: WalletRepository,
) {
    @Transactional
    fun createPaymentWithPessimisticLock(
        command: CreatePaymentCommand,
        action: () -> Payment,
    ): Payment {
        reservationRepository.getByTokenAndReservationIdWithPessimisticLockOrThrow(command.queueToken, command.reservationId)
        walletRepository.getOrCreateByQueueTokenWithPessimisticLockOrThrow(command.queueToken)
        return action.invoke()
    }

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 50, multiplier = 2.0, maxDelay = 1000),
    )
    fun createPaymentWithOptimisticLock(
        command: CreatePaymentCommand,
        action: () -> Payment,
    ): Payment {
        reservationRepository.getByTokenAndReservationIdWithOptimisticLockOrThrow(command.queueToken, command.reservationId)
        walletRepository.getOrCreateByQueueTokenWithOptimisticLockOrThrow(command.queueToken)
        return action.invoke()
    }
}
