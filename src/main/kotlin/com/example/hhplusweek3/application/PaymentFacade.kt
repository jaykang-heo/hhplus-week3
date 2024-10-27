package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.WalletService
import com.example.hhplusweek3.domain.validator.CreatePaymentCommandValidator
import org.springframework.stereotype.Service

@Service
class PaymentFacade(
    private val createPaymentCommandValidator: CreatePaymentCommandValidator,
    private val paymentRepository: PaymentRepository,
    private val reservationRepository: ReservationRepository,
    private val walletService: WalletService,
) {
    fun createPayment(command: CreatePaymentCommand): Payment {
        createPaymentCommandValidator.validate(command)
        val amount = reservationRepository.getByTokenAndReservationId(command.queueToken, command.reservationId).amount
        val payment = Payment(command, amount)
        paymentRepository.save(payment, command.queueToken)
        walletService.redeem(payment.amount, command.queueToken)
        return paymentRepository.save(payment, command.queueToken)
    }
}
