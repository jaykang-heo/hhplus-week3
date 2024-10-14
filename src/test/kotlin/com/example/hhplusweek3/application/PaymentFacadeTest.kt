package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Payment
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.PaymentRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.validator.CreatePaymentCommandValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.time.Instant

class PaymentFacadeTest {

    private val mockPaymentRepository = mock(PaymentRepository::class.java)
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val mockCreatePaymentCommandValidator = mock(CreatePaymentCommandValidator::class.java)
    private val sut = PaymentFacade(mockPaymentRepository, mockReservationRepository, mockCreatePaymentCommandValidator)

    @Test
    @DisplayName("결제 생성 명령 검증이 실패하면, 에러를 반환한다")
    fun `when command validation fails, then throw error`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")
        doThrow(IllegalArgumentException("Invalid command")).`when`(mockCreatePaymentCommandValidator).validate(command)

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.createPayment(command)
        }

        verify(mockCreatePaymentCommandValidator).validate(command)
    }

    @Test
    @DisplayName("예약이 존재하지 않으면, 에러를 반환한다")
    fun `when reservation does not exist, then throw error`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")
        `when`(mockReservationRepository.getByTokenAndReservationId("token", "reservationId")).thenThrow(RuntimeException("Reservation not found"))

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.createPayment(command)
        }

        verify(mockCreatePaymentCommandValidator).validate(command)
        verify(mockReservationRepository).getByTokenAndReservationId("token", "reservationId")
    }

    @Test
    @DisplayName("결제 생성이 성공하면, 생성된 결제를 반환한다")
    fun `when payment creation succeeds, then return created payment`() {
        // given
        val command = CreatePaymentCommand("token", "reservationId")
        val reservation = Reservation(CreateReservationCommand(command.queueToken, 100L, Instant.now()), 1000L)
        val createdPayment = Payment("paymentId", 1000L, "reservationId", Instant.now())

        `when`(mockReservationRepository.getByTokenAndReservationId("token", "reservationId")).thenReturn(reservation)
        `when`(mockPaymentRepository.save(any(), any())).thenReturn(createdPayment)

        // when
        val result = sut.createPayment(command)

        // then
        assertEquals(createdPayment, result)
        verify(mockCreatePaymentCommandValidator).validate(command)
        verify(mockReservationRepository).getByTokenAndReservationId("token", "reservationId")
        verify(mockPaymentRepository).save(any(), eq("token"))
    }
}
