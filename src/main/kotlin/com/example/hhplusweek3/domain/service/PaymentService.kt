package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.model.exception.AcquireLockFailedException
import com.example.hhplusweek3.domain.port.LockRepository
import org.springframework.stereotype.Component

@Component
class PaymentService(
    private val lockRepository: LockRepository,
) {
    fun createPaymentWithLockOrThrow(
        command: CreatePaymentCommand,
        action: () -> Payment,
    ): Payment =
        lockRepository.acquirePaymentLock(command.queueToken, command.reservationId) {
            lockRepository.acquireWalletLock(command.queueToken) {
                action.invoke()
            }
        } ?: throw AcquireLockFailedException("PaymentFacade::${command.queueToken}, ${command.reservationId}")
}
