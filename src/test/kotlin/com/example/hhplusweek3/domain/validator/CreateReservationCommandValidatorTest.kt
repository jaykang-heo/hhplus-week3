package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.ConcertSeatNotFoundException
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.InvalidReservationException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.service.ReservationService
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Instant

class CreateReservationCommandValidatorTest {
    private val mockConcertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val mockReservationService = mock(ReservationService::class.java)
    private val sut =
        CreateReservationCommandValidator(
            mockQueueRepository,
            mockConcertSeatRepository,
            mockReservationService,
        )

    @Test
    @DisplayName("대기열이 존재하지 않으면 QueueNotFoundException을 반환한다")
    fun `when queue does not exist, then throw QueueNotFoundException`() {
        // given
        val command = CreateReservationCommand("non-existent-token", 1L, Instant.now().plusSeconds(3600))
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        assertThrows(QueueNotFoundException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("대기열이 활성화되지 않았으면 InvalidQueueStatusException을 반환한다")
    fun `when queue is not active, then throw InvalidQueueStatusException`() {
        // given
        val command = CreateReservationCommand("inactive-token", 1L, Instant.now().plusSeconds(3600))
        val inactiveQueue =
            Queue(
                "inactive-token",
                QueueStatus.PENDING,
                Instant.now(),
                Instant.now(),
                Instant.now(),
            )
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        assertThrows(InvalidQueueStatusException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("콘서트 좌석이 존재하지 않으면 ConcertSeatNotFoundException을 반환한다")
    fun `when concert seat does not exist, then throw ConcertSeatNotFoundException`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val command = CreateReservationCommand("token", 1L, date)
        val activeQueue = Queue("token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())

        `when`(mockQueueRepository.findByToken("token")).thenReturn(activeQueue)
        `when`(mockConcertSeatRepository.existsByDateAndSeatNumber(date, 1L)).thenReturn(false)

        // when & then
        assertThrows(ConcertSeatNotFoundException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("유효하지 않은 예약이면 InvalidReservationException을 반환한다")
    fun `when reservation is invalid, then throw InvalidReservationException`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val command = CreateReservationCommand("token", 1L, date)
        val activeQueue = Queue("token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())

        `when`(mockQueueRepository.findByToken("token")).thenReturn(activeQueue)
        `when`(mockConcertSeatRepository.existsByDateAndSeatNumber(date, 1L)).thenReturn(true)
        `when`(mockReservationService.isValid(date, 1L, "token")).thenReturn(false)

        // when & then
        assertThrows(InvalidReservationException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("모든 조건이 만족되면 검증을 통과한다")
    fun `when all conditions are met, then validation passes`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val command = CreateReservationCommand("token", 1L, date)
        val activeQueue = Queue("token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())

        `when`(mockQueueRepository.findByToken("token")).thenReturn(activeQueue)
        `when`(mockConcertSeatRepository.existsByDateAndSeatNumber(date, 1L)).thenReturn(true)
        `when`(mockReservationService.isValid(date, 1L, "token")).thenReturn(true)

        // when & then
        assertDoesNotThrow {
            sut.validate(command)
        }
    }
}
