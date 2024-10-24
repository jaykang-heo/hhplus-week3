package com.example.hhplusweek3.application.unittest

import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.ConcertSeat
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.ReservationService
import com.example.hhplusweek3.domain.validator.CreateReservationCommandValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.time.Instant
import java.util.UUID
import kotlin.random.Random

class ReservationFacadeTest {
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val mockReservationService = mock(ReservationService::class.java)
    private val mockCreateReservationCommandValidator = mock(CreateReservationCommandValidator::class.java)
    private val mockConcertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val sut =
        ReservationFacade(
            mockReservationService,
            mockCreateReservationCommandValidator,
            mockReservationRepository,
            mockConcertSeatRepository,
        )

    @Test
    @DisplayName("예약 생성 시 락을 획득하고 만료된 예약을 삭제한 후 새로운 예약을 생성한다")
    fun `when creating reservation, acquire lock, delete expired and create new reservation`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)
        val amount = 100L
        val concertSeat = ConcertSeat(dateUtc, amount, seatNumber)
        val expectedReservation = Reservation(command, amount)

        // Mock the reserveWithLock to execute the provided lambda
        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Reservation>(1)
            lockCallback.invoke()
        }.`when`(mockReservationService).reserveWithLock(any(), any())

        `when`(mockConcertSeatRepository.getByDateAndSeatNumber(dateUtc, seatNumber)).thenReturn(concertSeat)
        `when`(mockReservationRepository.save(any())).thenReturn(expectedReservation)

        // when
        val result = sut.reserve(command)

        // then
        assertEquals(expectedReservation, result)
        verify(mockReservationService).reserveWithLock(any(), any())
        verify(mockReservationService).deleteIfExpired(dateUtc, seatNumber)
        verify(mockCreateReservationCommandValidator).validate(command)
        verify(mockConcertSeatRepository).getByDateAndSeatNumber(dateUtc, seatNumber)
        verify(mockReservationRepository).save(any())
    }

    @Test
    @DisplayName("예약 생성 정책 검증이 실패하면, 락 내에서 예외가 발생한다")
    fun `when create reservation policy validation fails within lock, then throw exception`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)

        doThrow(IllegalStateException("Policy validation failed"))
            .`when`(mockCreateReservationCommandValidator)
            .validate(command)

        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Reservation>(1)
            lockCallback.invoke()
        }.`when`(mockReservationService).reserveWithLock(any(), any())

        // when & then
        assertThrows(IllegalStateException::class.java) {
            sut.reserve(command)
        }

        verify(mockReservationService).reserveWithLock(any(), any())
        verify(mockReservationService).deleteIfExpired(dateUtc, seatNumber)
        verify(mockCreateReservationCommandValidator).validate(command)
        verifyNoInteractions(mockConcertSeatRepository)
        verifyNoInteractions(mockReservationRepository)
    }

    @Test
    @DisplayName("만료된 예약 삭제 중 예외가 발생하면, 예약 생성이 중단된다")
    fun `when delete expired reservation fails, then stop reservation process`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)

        doThrow(RuntimeException("Failed to delete expired reservation"))
            .`when`(mockReservationService)
            .deleteIfExpired(dateUtc, seatNumber)

        doAnswer { invocation ->
            val lockCallback = invocation.getArgument<() -> Reservation>(1)
            lockCallback.invoke()
        }.`when`(mockReservationService).reserveWithLock(any(), any())

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.reserve(command)
        }

        verify(mockReservationService).reserveWithLock(any(), any())
        verify(mockReservationService).deleteIfExpired(dateUtc, seatNumber)
        verifyNoInteractions(mockCreateReservationCommandValidator)
        verifyNoInteractions(mockConcertSeatRepository)
        verifyNoInteractions(mockReservationRepository)
    }
}
