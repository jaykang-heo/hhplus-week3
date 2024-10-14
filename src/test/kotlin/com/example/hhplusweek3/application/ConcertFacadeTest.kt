package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.model.ConcertSeat
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import com.example.hhplusweek3.domain.validator.GetAvailableConcertSeatsQueryValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.time.Instant
import java.util.UUID

class ConcertFacadeTest {

    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val mockConcertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val mockGetAvailableConcertSeatsQueryValidator = mock(GetAvailableConcertSeatsQueryValidator::class.java)
    private val sut = ConcertFacade(mockConcertSeatRepository, mockReservationRepository, mockGetAvailableConcertSeatsQueryValidator)

    @Test
    @DisplayName("예약 가능한 좌석을 찾을때 정책 검증기가 에러를 반환하면, 실행을 중단한다")
    fun `when query validator fails, then stop`() {
        // given
        val query = FindAvailableConcertSeatsQuery(Instant.now())
        doThrow(RuntimeException("Validation failed")).`when`(mockGetAvailableConcertSeatsQueryValidator).validate(query)

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.findAvailableSeats(query)
        }

        verify(mockGetAvailableConcertSeatsQueryValidator).validate(query)
        verifyNoInteractions(mockReservationRepository, mockConcertSeatRepository)
    }

    @Test
    @DisplayName("모든 좌석이 예약되었다면 예약 가능한 좌석은 빈 값으로 반환한다")
    fun `when all seats are reserved, then available seats list return empty`() {
        // given
        val now = Instant.now()
        val query = FindAvailableConcertSeatsQuery(now)
        val allSeats = listOf(
            ConcertSeat(now, 1L),
            ConcertSeat(now, 2L),
            ConcertSeat(now, 3L)
        )
        val reservedSeats = listOf(
            Reservation(UUID.randomUUID().toString(), null, "token1", now, 1L, now, now, now.plusSeconds(300)),
            Reservation(UUID.randomUUID().toString(), null, "token2", now, 2L, now, now, now.plusSeconds(300)),
            Reservation(UUID.randomUUID().toString(), null, "token3", now, 3L, now, now, now.plusSeconds(300))
        )

        `when`(mockReservationRepository.findAllByDate(query.dateUtc)).thenReturn(reservedSeats)
        `when`(mockConcertSeatRepository.findByDate(query.dateUtc)).thenReturn(allSeats)

        // when
        val result = sut.findAvailableSeats(query)

        // then
        assertTrue(result.availableSeats.isEmpty())
        assertEquals(allSeats, result.allSeats)
    }

    @Test
    @DisplayName("예약 가능한 좌석이 있다면, 예약 가능한 좌석을 반환한다")
    fun `when there are available seats, then return available seats list`() {
        // given
        val now = Instant.now()
        val query = FindAvailableConcertSeatsQuery(now)
        val allSeats = listOf(
            ConcertSeat(now, 1L),
            ConcertSeat(now, 2L),
            ConcertSeat(now, 3L)
        )
        val reservedSeats = listOf(
            Reservation(UUID.randomUUID().toString(), null, "token1", now, 1L, now, now, now.plusSeconds(300))
        )

        `when`(mockReservationRepository.findAllByDate(query.dateUtc)).thenReturn(reservedSeats)
        `when`(mockConcertSeatRepository.findByDate(query.dateUtc)).thenReturn(allSeats)

        // when
        val result = sut.findAvailableSeats(query)

        // then
        assertEquals(listOf(ConcertSeat(now, 2L), ConcertSeat(now, 3L)), result.availableSeats)
        assertEquals(allSeats, result.allSeats)
    }
}
