package com.example.hhplusweek3.application.unittest

import com.example.hhplusweek3.application.PaymentFacade
import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.PaymentService
import com.example.hhplusweek3.domain.service.WalletService
import com.example.hhplusweek3.domain.validator.CreatePaymentCommandValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.time.Instant

class PaymentFacadeTest {
    private val mockPaymentRepository = mock(PaymentRepository::class.java)
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val mockCreatePaymentCommandValidator = mock(CreatePaymentCommandValidator::class.java)
    private val mockWalletService = mock(WalletService::class.java)
    private val mockPaymentService = mock(PaymentService::class.java)
    private val sut =
        PaymentFacade(
            mockPaymentService,
            mockWalletService,
            mockCreatePaymentCommandValidator,
            mockPaymentRepository,
            mockReservationRepository,
        )

    @Test
    @DisplayName("결제 생성 시 락을 획득하고 지갑에서 차감 후 결제를 저장한다")
    fun `when creating payment, acquire lock, redeem wallet and save payment twice`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")
        val reservation = Reservation(CreateReservationCommand(command.queueToken, 100L, Instant.now()), 1000L)
        val payment = Payment(command, reservation.amount)

        // Mock the lock execution to run the provided lambda
        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Payment>(1)
            lockCallback.invoke()
        }.`when`(mockPaymentService).createPaymentWithLockOrThrow(any(), any())

        `when`(mockReservationRepository.getByTokenAndReservationId("token", "reservationId")).thenReturn(reservation)
        `when`(mockPaymentRepository.save(any())).thenReturn(payment)

        // when
        val result = sut.createPayment(command)

        // then
        assertEquals(payment, result)

        // Verify the complete flow within lock
        verify(mockPaymentService).createPaymentWithLockOrThrow(eq(command), any())
        verify(mockCreatePaymentCommandValidator).validate(command)
        verify(mockReservationRepository).getByTokenAndReservationId("token", "reservationId")
        verify(mockPaymentRepository, times(1)).save(any())
        verify(mockWalletService).redeem(reservation.amount, "token")
    }

    @Test
    @DisplayName("결제 생성 명령 검증이 실패하면, 락 내에서 예외가 발생한다")
    fun `when command validation fails within lock, then throw error`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")

        doThrow(IllegalArgumentException("Invalid command"))
            .`when`(mockCreatePaymentCommandValidator)
            .validate(command)

        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Payment>(1)
            lockCallback.invoke()
        }.`when`(mockPaymentService).createPaymentWithLockOrThrow(any(), any())

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.createPayment(command)
        }

        verify(mockPaymentService).createPaymentWithLockOrThrow(eq(command), any())
        verify(mockCreatePaymentCommandValidator).validate(command)
        verifyNoInteractions(mockReservationRepository)
        verifyNoInteractions(mockWalletService)
        verifyNoInteractions(mockPaymentRepository)
    }

    @Test
    @DisplayName("예약이 존재하지 않으면, 락 내에서 예외가 발생한다")
    fun `when reservation does not exist, then throw error within lock`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")

        `when`(mockReservationRepository.getByTokenAndReservationId("token", "reservationId"))
            .thenThrow(RuntimeException("Reservation not found"))

        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Payment>(1)
            lockCallback.invoke()
        }.`when`(mockPaymentService).createPaymentWithLockOrThrow(any(), any())

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.createPayment(command)
        }

        verify(mockPaymentService).createPaymentWithLockOrThrow(eq(command), any())
        verify(mockCreatePaymentCommandValidator).validate(command)
        verify(mockReservationRepository).getByTokenAndReservationId("token", "reservationId")
        verifyNoInteractions(mockWalletService)
        verifyNoInteractions(mockPaymentRepository)
    }

    @Test
    @DisplayName("지갑 차감이 실패하면, 락 내에서 예외가 발생한다")
    fun `when wallet redemption fails, throw error within lock`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")
        val reservation = Reservation(CreateReservationCommand(command.queueToken, 100L, Instant.now()), 1000L)
        val payment = Payment(command, reservation.amount)

        `when`(mockReservationRepository.getByTokenAndReservationId("token", "reservationId")).thenReturn(reservation)
        `when`(mockPaymentRepository.save(any())).thenReturn(payment)
        doThrow(RuntimeException("Insufficient balance"))
            .`when`(mockWalletService)
            .redeem(reservation.amount, command.queueToken)

        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Payment>(1)
            lockCallback.invoke()
        }.`when`(mockPaymentService).createPaymentWithLockOrThrow(any(), any())

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.createPayment(command)
        }

        verify(mockPaymentService).createPaymentWithLockOrThrow(eq(command), any())
        verify(mockCreatePaymentCommandValidator).validate(command)
        verify(mockReservationRepository).getByTokenAndReservationId("token", "reservationId")
        verify(mockWalletService).redeem(reservation.amount, command.queueToken)
        verify(mockPaymentRepository, times(0)).save(any())
    }
}
