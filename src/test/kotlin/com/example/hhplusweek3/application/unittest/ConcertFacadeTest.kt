package com.example.hhplusweek3.application.unittest

import com.example.hhplusweek3.application.ConcertFacade
import com.example.hhplusweek3.domain.model.Concert.Schedule
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import com.example.hhplusweek3.domain.service.ConcertService
import com.example.hhplusweek3.domain.validator.GetAvailableConcertSeatsQueryValidator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.time.Instant

class ConcertFacadeTest {
    private val mockConcertService = mock(ConcertService::class.java)
    private val mockGetAvailableConcertSeatsQueryValidator = mock(GetAvailableConcertSeatsQueryValidator::class.java)
    private val sut =
        ConcertFacade(
            concertService = mockConcertService,
            getAvailableConcertSeatsQueryValidator = mockGetAvailableConcertSeatsQueryValidator,
        )

    @Test
    @DisplayName("예약 가능한 좌석을 찾을 때 검증기가 에러를 반환하면, 실행을 중단한다")
    fun `when query validator fails, then stop`() {
        // given
        val query = FindAvailableConcertSeatsQuery(Instant.now())
        Mockito
            .doThrow(RuntimeException("Validation failed"))
            .`when`(mockGetAvailableConcertSeatsQueryValidator)
            .validate(query)

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.findAvailableSeats(query)
        }

        Mockito.verify(mockGetAvailableConcertSeatsQueryValidator).validate(query)
        Mockito.verifyNoInteractions(mockConcertService)
    }

    @Test
    @DisplayName("모든 좌석이 예약되었다면 예약 가능한 좌석은 빈 값으로 반환한다")
    fun `when all seats are reserved, then available seats list is empty`() {
        // given
        val now = Instant.now()
        val query = FindAvailableConcertSeatsQuery(now)
        val allSeats =
            listOf(
                Schedule.Seat(number = 1L),
                Schedule.Seat(number = 2L),
                Schedule.Seat(number = 3L),
            )
        val availableSeats = emptyList<Schedule.Seat>()

        `when`(mockConcertService.getAvailableSeatsByDate(query.dateUtc)).thenReturn(availableSeats)
        `when`(mockConcertService.getAllSeatsByDate(query.dateUtc)).thenReturn(allSeats)

        // when
        val result = sut.findAvailableSeats(query)

        // then
        Assertions.assertTrue(
            result.availableSchedules
                .first()
                .seats
                .isEmpty(),
        )
        assertEquals(allSeats, result.allSchedules.first().seats)
        Mockito.verify(mockGetAvailableConcertSeatsQueryValidator).validate(query)
        Mockito.verify(mockConcertService).getAvailableSeatsByDate(query.dateUtc)
        Mockito.verify(mockConcertService).getAllSeatsByDate(query.dateUtc)
    }

    @Test
    @DisplayName("예약 가능한 좌석이 있다면, 예약 가능한 좌석을 반환한다")
    fun `when there are available seats, then return available seats list`() {
        // given
        val now = Instant.now()
        val query = FindAvailableConcertSeatsQuery(now)
        val allSeats =
            listOf(
                Schedule.Seat(number = 1L),
                Schedule.Seat(number = 2L),
                Schedule.Seat(number = 3L),
            )
        val availableSeats =
            listOf(
                Schedule.Seat(number = 2L),
                Schedule.Seat(number = 3L),
            )

        `when`(mockConcertService.getAvailableSeatsByDate(query.dateUtc)).thenReturn(availableSeats)
        `when`(mockConcertService.getAllSeatsByDate(query.dateUtc)).thenReturn(allSeats)

        // when
        val result = sut.findAvailableSeats(query)

        // then
        assertEquals(availableSeats, result.availableSchedules.first().seats)
        assertEquals(allSeats, result.allSchedules.first().seats)
        Mockito.verify(mockGetAvailableConcertSeatsQueryValidator).validate(query)
        Mockito.verify(mockConcertService).getAvailableSeatsByDate(query.dateUtc)
        Mockito.verify(mockConcertService).getAllSeatsByDate(query.dateUtc)
    }

    @Test
    @DisplayName("모든 날짜가 예약되었다면 예약 가능한 스케줄은 빈 값으로 반환한다")
    fun `when all dates are reserved, then available schedules list is empty`() {
        // given
        val now = Instant.now()
        val allSchedules =
            listOf(
                Schedule(date = now, seats = listOf()),
                Schedule(date = now.plusSeconds(86400), seats = listOf()),
            )
        val availableSchedules = emptyList<Schedule>()

        `when`(mockConcertService.getAvailableSchedules()).thenReturn(availableSchedules)
        `when`(mockConcertService.getAllSchedules()).thenReturn(allSchedules)

        // when
        val result = sut.findAvailableDates()

        // then
        Assertions.assertTrue(result.availableSchedules.isEmpty())
        assertEquals(allSchedules, result.allSchedules)
        Mockito.verify(mockConcertService).getAvailableSchedules()
        Mockito.verify(mockConcertService).getAllSchedules()
    }

    @Test
    @DisplayName("예약 가능한 날짜가 있다면, 예약 가능한 스케줄을 반환한다")
    fun `when there are available dates, then return available schedules list`() {
        // given
        val now = Instant.now()
        val availableSchedules =
            listOf(
                Schedule(date = now.plusSeconds(86400), seats = listOf()),
                Schedule(date = now.plusSeconds(172800), seats = listOf()),
            )
        val allSchedules =
            listOf(
                Schedule(date = now, seats = listOf()),
                Schedule(date = now.plusSeconds(86400), seats = listOf()),
                Schedule(date = now.plusSeconds(172800), seats = listOf()),
            )

        `when`(mockConcertService.getAvailableSchedules()).thenReturn(availableSchedules)
        `when`(mockConcertService.getAllSchedules()).thenReturn(allSchedules)

        // when
        val result = sut.findAvailableDates()

        // then
        assertEquals(availableSchedules, result.availableSchedules)
        assertEquals(allSchedules, result.allSchedules)
        Mockito.verify(mockConcertService).getAvailableSchedules()
        Mockito.verify(mockConcertService).getAllSchedules()
    }

    @Test
    @DisplayName("콘서트 스케줄이 없다면, 빈 리스트를 반환한다")
    fun `when there are no concert schedules, then return empty lists`() {
        // given
        `when`(mockConcertService.getAvailableSchedules()).thenReturn(emptyList())
        `when`(mockConcertService.getAllSchedules()).thenReturn(emptyList())

        // when
        val result = sut.findAvailableDates()

        // then
        Assertions.assertTrue(result.availableSchedules.isEmpty())
        Assertions.assertTrue(result.allSchedules.isEmpty())
        Mockito.verify(mockConcertService).getAvailableSchedules()
        Mockito.verify(mockConcertService).getAllSchedules()
    }
}
