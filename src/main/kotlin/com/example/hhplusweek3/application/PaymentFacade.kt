package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.domain.port.RedisRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.PaymentService
import com.example.hhplusweek3.domain.service.WalletService
import com.example.hhplusweek3.domain.validator.CreatePaymentCommandValidator
import org.springframework.stereotype.Service

@Service
class PaymentFacade(
    private val paymentService: PaymentService,
    private val walletService: WalletService,
    private val createPaymentCommandValidator: CreatePaymentCommandValidator,
    private val paymentRepository: PaymentRepository,
    private val reservationRepository: ReservationRepository,
    private val redisRepository: RedisRepository,
) {
    fun createPayment(command: CreatePaymentCommand): Payment =
        redisRepository.redLock(command.queueToken) {
            createPaymentCommandValidator.validate(command)
            val amount = reservationRepository.getByTokenAndReservationId(command.queueToken, command.reservationId).amount
            val payment = Payment(command, amount)
            walletService.redeem(payment.amount, command.queueToken)
            paymentRepository.save(payment, command.queueToken)
        }
            ?: throw IllegalStateException("Lock acquire failed for ${command.queueToken}")
}
