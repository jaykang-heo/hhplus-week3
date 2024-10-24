package com.example.hhplusweek3.application.integrationtest.concurrency

import com.example.hhplusweek3.application.PaymentFacade
import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.repository.jpa.PaymentEntityJpaRepository
import com.example.hhplusweek3.testservice.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaymentFacadeCreatePaymentConcurrencyIntegrationTest(
    @Autowired private val paymentFacade: PaymentFacade,
    @Autowired private val paymentEntityJpaRepository: PaymentEntityJpaRepository,
    @Autowired private val walletRepository: WalletRepository,
    @Autowired private val testUtils: TestUtils,
) {
    @Test
    fun `한명이 동시에 여러 결제를 할떄, 한번만 성공한다`() {
        // given
        val count = 10
        testUtils.resetDatabase()
        val reservation = testUtils.createReservation()
        val commands = (1..count).map { CreatePaymentCommand(reservation.queueToken, reservation.id) }

        // when
        testUtils.asyncRun(count, commands) {
            paymentFacade.createPayment(it)
        }

        // then
        val actualPayment =
            paymentEntityJpaRepository.findAll().filter {
                it.queueToken == reservation.queueToken &&
                    it.reservationId == reservation.id
            }
        assertThat(actualPayment).hasSize(1)
    }

    @Test
    fun `한명이 동시에 여러 결제를 할때 지갑 잔액을 초과한다면, 지갑 잔액만큼만 결제된다`() {
        // given
        val expectedPaymentCount = 10
        val count = expectedPaymentCount * 2
        val chargeAmount = TestUtils.SEAT_PRICE * expectedPaymentCount
        testUtils.resetDatabase()
        val queueToken = testUtils.issueQueueToken()
        val reservations = testUtils.createReservations(count, queueToken, chargeAmount)
        assertThat(reservations).hasSize(20)
        val commands = reservations.map { CreatePaymentCommand(it.queueToken, it.id) }

        // when
        testUtils.asyncRun(count, commands) {
            paymentFacade.createPayment(it)
        }

        // then
        val actualPayment =
            paymentEntityJpaRepository.findAll().filter {
                it.queueToken == queueToken &&
                    reservations.map { reservation -> reservation.id }.contains(it.reservationId)
            }
        val actualWalletBalance = walletRepository.getByQueueToken(queueToken).balance
        assertThat(actualPayment).hasSize(10)
        assertThat(actualWalletBalance).isZero()
    }

    @Test
    fun `여러명이 동시에 다른 결제를 할때, 모두 각각 정상적으로 결제된다`() {
        // given
        val count = 10
        testUtils.resetDatabase()
        val reservations = testUtils.createReservations(count)
        val commands = reservations.map { CreatePaymentCommand(it.queueToken, it.id) }

        // when
        testUtils.asyncRun(count, commands) {
            paymentFacade.createPayment(it)
        }

        // then
        val actualPayment =
            paymentEntityJpaRepository.findAll().filter { paymentEntity ->
                reservations.map { it.queueToken }.contains(paymentEntity.queueToken) &&
                    reservations.map { it.id }.contains(paymentEntity.reservationId)
            }
        assertThat(actualPayment).hasSize(count)
    }
}
