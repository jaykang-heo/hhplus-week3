package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
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
    private val sut = CreatePaymentCommandValidator(mockReservationRepository, mockQueueRepository)

    @Test
    @DisplayName("큐 토큰이 비어있으면, 에러를 반환한다")
    fun `when queue token is blank, then throw error`() {
        // given
        val command = CreatePaymentCommand(" ", "reservationId")

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("예약 ID가 비어있으면, 에러를 반환한다")
    fun `when reservation id is blank, then throw error`() {
        // given
        val command = CreatePaymentCommand("queueToken", " ")

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("큐가 존재하지 않으면, 에러를 반환한다")
    fun `when queue does not exist, then throw error`() {
        // given
        val command = CreatePaymentCommand("non-existent-token", "reservationId")
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("queue token not found"))
    }

    @Test
    @DisplayName("큐가 활성 상태가 아니면, 에러를 반환한다")
    fun `when queue is not active, then throw error`() {
        // given
        val command = CreatePaymentCommand("inactive-token", "reservationId")
        val inactiveQueue = Queue("inactive-token", QueueStatus.PENDING, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("queue is not active"))
    }

    @Test
    @DisplayName("예약이 존재하지 않으면, 에러를 반환한다")
    fun `when reservation does not exist, then throw error`() {
        // given
        val command = CreatePaymentCommand("active-token", "non-existent-reservation")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)
        `when`(mockReservationRepository.findByTokenAndReservationId("active-token", "non-existent-reservation")).thenReturn(null)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("reservation token not found"))
    }

    @Test
    @DisplayName("모든 조건이 만족되면, 검증을 통과한다")
    fun `when all conditions are met, then validation passes`() {
        // given
        val command = CreatePaymentCommand("active-token", "valid-reservation")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        val validReservation = Reservation(CreateReservationCommand(activeQueue.token, 100L, Instant.now()), 1000L)
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)
        `when`(mockReservationRepository.findByTokenAndReservationId("active-token", "valid-reservation")).thenReturn(validReservation)

        // when & then
        assertDoesNotThrow {
            sut.validate(command)
        }
    }
}
