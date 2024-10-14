package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Instant

class CreateReservationCommandValidatorTest {

    private val mockConcertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val sut = CreateReservationCommandValidator(mockReservationRepository, mockQueueRepository, mockConcertSeatRepository)

    @Test
    @DisplayName("대기열이 존재하지 않는다면 에러를 반환한다")
    fun `when queue does not exist by given token, then throw error`() {
        // given
        val command = CreateReservationCommand("non-existent-token", 1L, Instant.now().plusSeconds(3600))
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("not found"))
    }

    @Test
    @DisplayName("대기열이 활성화 되지 않았다면, 에러를 반환한다")
    fun `when queue is not active, then throw error`() {
        // given
        val command = CreateReservationCommand("inactive-token", 1L, Instant.now().plusSeconds(3600))
        val inactiveQueue = Queue("inactive-token", QueueStatus.PENDING, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("queue status must be active"))
    }

    @Test
    @DisplayName("콘서트가 주어진 날짜와 좌석으로 존재하지 않는다면, 에러를 반환한다")
    fun `when concert does not exist by given date and seat, then throw error`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val command = CreateReservationCommand("token", 1L, date)
        val activeQueue = Queue("token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("token")).thenReturn(activeQueue)
        `when`(mockConcertSeatRepository.existsByDateAndSeatNumber(date, 1L)).thenReturn(false)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("concert by date"))
    }

    @Test
    @DisplayName("주어진 날짜와 좌석으로 예약된 내역이 존재한다면, 에러를 반환한다")
    fun `when reservation exists by given date and seat, then throw error`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val command = CreateReservationCommand("token", 1L, date)
        val activeQueue = Queue("token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("token")).thenReturn(activeQueue)
        `when`(mockConcertSeatRepository.existsByDateAndSeatNumber(date, 1L)).thenReturn(true)
        `when`(mockReservationRepository.findReservationBySeatNumberAndDate(date, 1L)).thenReturn(Reservation(command))

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("already reserved"))
    }

    @Test
    @DisplayName("대기열 토큰으로 이미 예약한 좌석이 있다면, 에러를 반환한다")
    fun `when there already is a reservation by given queue token, then throw error`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val command = CreateReservationCommand("token", 1L, date)
        val activeQueue = Queue("token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        val existingReservation = Reservation(CreateReservationCommand("token", 2L, date))
        `when`(mockQueueRepository.findByToken("token")).thenReturn(activeQueue)
        `when`(mockConcertSeatRepository.existsByDateAndSeatNumber(date, 1L)).thenReturn(true)
        `when`(mockReservationRepository.findReservationBySeatNumberAndDate(date, 1L)).thenReturn(null)
        `when`(mockReservationRepository.findByToken("token")).thenReturn(existingReservation)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("already reserved seat"))
    }
}
