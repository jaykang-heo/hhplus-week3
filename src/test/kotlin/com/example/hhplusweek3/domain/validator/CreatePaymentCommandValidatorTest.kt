package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.model.exception.InsufficientBalanceException
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.model.exception.ReservationNotFoundException
import com.example.hhplusweek3.domain.model.exception.WalletNotFoundException
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.port.WalletRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Instant

class CreatePaymentCommandValidatorTest {
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val mockWalletRepository = mock(WalletRepository::class.java)
    private val mockPaymentRepository = mock(PaymentRepository::class.java)
    private val sut =
        CreatePaymentCommandValidator(mockReservationRepository, mockPaymentRepository, mockQueueRepository, mockWalletRepository)

    @Test
    @DisplayName("큐가 존재하지 않으면, QueueNotFoundException을 반환한다")
    fun `when queue does not exist, then throw QueueNotFoundException`() {
        // given
        val command = CreatePaymentCommand("non-existent-token", "reservationId")
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        assertThrows(QueueNotFoundException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("큐가 활성 상태가 아니면, InvalidQueueStatusException을 반환한다")
    fun `when queue is not active, then throw InvalidQueueStatusException`() {
        // given
        val command = CreatePaymentCommand("inactive-token", "reservationId")
        val inactiveQueue = Queue("inactive-token", QueueStatus.PENDING, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        assertThrows(InvalidQueueStatusException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("예약이 존재하지 않으면, ReservationNotFoundException을 반환한다")
    fun `when reservation does not exist, then throw ReservationNotFoundException`() {
        // given
        val command = CreatePaymentCommand("active-token", "non-existent-reservation")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)
        `when`(mockReservationRepository.findByTokenAndReservationId("active-token", "non-existent-reservation")).thenReturn(null)

        // when & then
        assertThrows(ReservationNotFoundException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("지갑이 존재하지 않으면, WalletNotFoundException을 반환한다")
    fun `when wallet does not exist, then throw WalletNotFoundException`() {
        // given
        val command = CreatePaymentCommand("active-token", "valid-reservation")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        val reservationCommand = CreateReservationCommand("active-token", 1L, Instant.now())
        val validReservation = Reservation(reservationCommand, 100L)
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)
        `when`(mockReservationRepository.findByTokenAndReservationId("active-token", "valid-reservation")).thenReturn(validReservation)
        `when`(mockWalletRepository.findByQueueToken("active-token")).thenReturn(null)

        // when & then
        assertThrows(WalletNotFoundException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("지갑 잔액이 예약 금액보다 작으면, InsufficientBalanceException을 반환한다")
    fun `when wallet balance is less than reservation amount, then throw InsufficientBalanceException`() {
        // given
        val command = CreatePaymentCommand("active-token", "valid-reservation")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        val reservationCommand = CreateReservationCommand("active-token", 1L, Instant.now())
        val validReservation = Reservation(reservationCommand, 100L)
        val insufficientWallet = Wallet(50L, "active-token")
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)
        `when`(mockReservationRepository.findByTokenAndReservationId("active-token", "valid-reservation")).thenReturn(validReservation)
        `when`(mockWalletRepository.findByQueueToken("active-token")).thenReturn(insufficientWallet)

        // when & then
        assertThrows(InsufficientBalanceException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("모든 조건이 만족되면, 검증을 통과한다")
    fun `when all conditions are met, then validation passes`() {
        // given
        val command = CreatePaymentCommand("active-token", "valid-reservation")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        val reservationCommand = CreateReservationCommand("active-token", 1L, Instant.now())
        val validReservation = Reservation(reservationCommand, 100L)
        val sufficientWallet = Wallet(200L, "active-token")
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)
        `when`(mockReservationRepository.findByTokenAndReservationId("active-token", "valid-reservation")).thenReturn(validReservation)
        `when`(mockWalletRepository.findByQueueToken("active-token")).thenReturn(sufficientWallet)

        // when & then
        assertDoesNotThrow {
            sut.validate(command)
        }
    }
}
