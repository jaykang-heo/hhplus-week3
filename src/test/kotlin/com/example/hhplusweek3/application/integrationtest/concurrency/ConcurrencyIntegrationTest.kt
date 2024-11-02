package com.example.hhplusweek3.application.integrationtest.concurrency

import com.example.hhplusweek3.application.PaymentFacade
import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.application.WalletFacade
import com.example.hhplusweek3.config.IntegrationTest
import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import com.example.hhplusweek3.repository.jpa.PaymentEntityJpaRepository
import com.example.hhplusweek3.testservice.TestUtils
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.time.measureTime

@Disabled
@IntegrationTest
class ConcurrencyIntegrationTest(
    @Autowired private val walletFacade: WalletFacade,
    @Autowired private val paymentFacade: PaymentFacade,
    @Autowired private val paymentEntityJpaRepository: PaymentEntityJpaRepository,
    @Autowired private val reservationFacade: ReservationFacade,
    @Autowired private val reservationRepository: ReservationRepository,
    @Autowired private val testUtils: TestUtils,
) {
    private val log = KotlinLogging.logger {}
    private val threadList = listOf(10, 100, 1000, 4000)

    @Test
    fun `결제 동시성 통합 테스트 성능 측정`() {
        // given
        val threadCounts = threadList
        threadCounts.forEach { threadCount ->
            testUtils.resetDatabase()
            val reservation = testUtils.createReservation()
            val commands = (1..threadCount).map { CreatePaymentCommand(reservation.queueToken, reservation.id) }

            // when
            val duration =
                measureTime {
                    testUtils.asyncRun(threadCount, commands) {
                        paymentFacade.createPayment(it)
                    }
                }

            // then
            val actualPayment =
                paymentEntityJpaRepository.findAll().filter {
                    it.queueToken == reservation.queueToken &&
                        it.reservationId == reservation.id
                }
            assertThat(actualPayment).hasSize(1)
            log.info { "결제 동시성 테스트 threadCount: $threadCount, duration: $duration" }
        }
        log.info { "=============================================================" }
    }

    @Test
    fun `예약 동시성 통합 테스트 성능 측정`() {
        // given
        val threadCounts = threadList
        threadCounts.forEach { threadCount ->
            testUtils.resetConcertSeats()
            testUtils.resetDatabase()
            val concertDate =
                LocalDate
                    .now()
                    .atStartOfDay()
                    .plusDays(2)
                    .toInstant(ZoneOffset.UTC)
            val seatNumber = 2L
            val queueToken = testUtils.issueQueueToken()
            val commands = (1..threadCount).map { CreateReservationCommand(queueToken, seatNumber, concertDate) }

            // when
            val duration =
                measureTime {
                    testUtils.asyncRun(threadCount, commands) { command ->
                        reservationFacade.reserve(command)
                    }
                }

            // then
            val actualReservations =
                reservationRepository.findAll().map {
                    it.queueToken == queueToken && it.reservedSeat == seatNumber && it.dateTimeUtc == concertDate
                }
            assertThat(actualReservations).hasSize(1)
            log.info { "예약 동시성 테스트 threadCount: $threadCount, duration: $duration" }
        }
        log.info { "=============================================================" }
    }

    @Test
    fun `지갑 동시성 통합 테스트 성능 측정`() {
        // given
        val threadCounts = threadList
        threadCounts.forEach { threadCount ->
            val amount = 1000L
            val expectedAmount = amount * threadCount
            testUtils.resetConcertSeats()
            val queueToken = testUtils.issueAndActivateQueueToken().token
            val initialBalance = walletFacade.get(GetWalletBalanceQuery(queueToken))
            assertThat(initialBalance.balance).isEqualTo(0)
            val commands = (1..threadCount).map { ChargeWalletCommand(amount, queueToken) }
            assertThat(commands.size).isEqualTo(threadCount)

            // when
            val duration =
                measureTime {
                    testUtils.asyncRun(threadCount, commands) { command ->
                        walletFacade.charge(command)
                    }
                }

            // then
            val actualWallet = walletFacade.get(GetWalletBalanceQuery(queueToken))
            assertThat(actualWallet.balance).isEqualTo(expectedAmount)
            log.info { "지갑 동시성 테스트 threadCount: $threadCount, duration: $duration" }
        }
        log.info { "=============================================================" }
    }
}
