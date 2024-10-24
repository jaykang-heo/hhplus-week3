package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.AlreadyPaidException
import com.example.hhplusweek3.domain.model.exception.InsufficientBalanceException
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.model.exception.ReservationNotFoundException
import com.example.hhplusweek3.domain.model.exception.WalletNotFoundException
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.port.WalletRepository
import org.springframework.stereotype.Component

@Component
class CreatePaymentCommandValidator(
    private val reservationRepository: ReservationRepository,
    private val paymentRepository: PaymentRepository,
    private val queueRepository: QueueRepository,
    private val walletRepository: WalletRepository,
) {
    fun validate(command: CreatePaymentCommand) {
        command.validate()

        val queue =
            queueRepository.findByToken(command.queueToken)
                ?: throw QueueNotFoundException(command.queueToken)

        if (queue.status != QueueStatus.ACTIVE) {
            throw InvalidQueueStatusException(queue.status)
        }

        val reservation =
            reservationRepository.findByTokenAndReservationId(command.queueToken, command.reservationId)
                ?: throw ReservationNotFoundException(command.queueToken, command.reservationId)

        val wallet =
            walletRepository.findByQueueToken(command.queueToken)
                ?: throw WalletNotFoundException(command.queueToken)

        if (wallet.balance < reservation.amount) {
            throw InsufficientBalanceException(wallet.balance, reservation.amount)
        }

        val payment = paymentRepository.findByQueueTokenAndReservationId(command.queueToken, command.reservationId)
        if (payment != null) {
            throw AlreadyPaidException(command.queueToken, payment.reservationId)
        }
    }
}
