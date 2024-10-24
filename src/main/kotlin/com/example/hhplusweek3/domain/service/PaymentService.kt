package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.port.WalletRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class PaymentService(
    private val reservationRepository: ReservationRepository,
    private val walletRepository: WalletRepository,
) {
    @Transactional
    fun createPaymentWithLock(
        command: CreatePaymentCommand,
        action: () -> Payment,
    ): Payment {
        reservationRepository.getByTokenAndReservationIdWithLockOrThrow(command.queueToken, command.reservationId)
        walletRepository.getOrCreateByQueueTokenWithLockOrThrow(command.queueToken)
        return action.invoke()
    }
}
