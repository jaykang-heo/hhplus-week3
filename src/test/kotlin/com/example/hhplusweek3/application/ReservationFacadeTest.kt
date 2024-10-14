package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.service.ReservationService
import com.example.hhplusweek3.domain.validator.CreateReservationCommandValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID
import kotlin.random.Random

class ReservationFacadeTest {

    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val mockQueueService = mock(QueueService::class.java)
    private val mockReservationService = mock(ReservationService::class.java)
    private val mockCreateReservationCommandValidator = mock(CreateReservationCommandValidator::class.java)
    private val sut = ReservationFacade(mockReservationRepository, mockQueueService, mockReservationService, mockCreateReservationCommandValidator)

    @Test
    @DisplayName("예약 생성 작업 중 대기열 선행 작업이 실패했을때, 실행을 멈춘다")
    fun `when queue pre run fails, then stop`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)
        doThrow(RuntimeException("Queue pre-run failed")).`when`(mockQueueService).preRun(any())

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.reserve(command)
        }

        verify(mockQueueService).preRun(any())
        verify(mockReservationService, never()).preRun()
        verify(mockCreateReservationCommandValidator, never()).validate(any())
        verify(mockReservationRepository, never()).save(any())
    }

    @Test
    @DisplayName("예약 생성 작업 중 예약 선행 작업이 실패했을때, 실행을 멈춘다")
    fun `when reservation pre run fails, then stop`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)
        doThrow(RuntimeException("Reservation pre-run failed")).`when`(mockReservationService).preRun()

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.reserve(command)
        }

        verify(mockQueueService).preRun(any())
        verify(mockReservationService).preRun()
        verify(mockCreateReservationCommandValidator, never()).validate(any())
        verify(mockReservationRepository, never()).save(any())
    }

    @Test
    @DisplayName("예약 생성 정책 검증이 실패하면, 실행을 멈춘다")
    fun `when create reservation policy validation fails, then stop`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)
        doThrow(IllegalStateException("Policy validation failed")).`when`(mockCreateReservationCommandValidator).validate(any())

        // when & then
        assertThrows(IllegalStateException::class.java) {
            sut.reserve(command)
        }

        verify(mockQueueService).preRun(any())
        verify(mockReservationService).preRun()
        verify(mockCreateReservationCommandValidator).validate(any())
        verify(mockReservationRepository, never()).save(any())
    }

    @Test
    @DisplayName("정상적으로 예약 생성을 하면, 저장된 예약 내역이 반환된다")
    fun `when valid create reservation, then return saved reservation`() {
        // given
        val token = UUID.randomUUID().toString()
        val seatNumber = Random.nextLong()
        val dateUtc = Instant.now().plusSeconds(10)
        val command = CreateReservationCommand(token, seatNumber, dateUtc)
        val expectedReservation = Reservation(command)
        `when`(mockReservationRepository.save(any())).thenReturn(expectedReservation)

        // when
        val result = sut.reserve(command)

        // then
        assertEquals(expectedReservation, result)
        verify(mockQueueService).preRun(any())
        verify(mockReservationService).preRun()
        verify(mockCreateReservationCommandValidator).validate(any())
        verify(mockReservationRepository).save(any())
    }
}
