package com.example.hhplusweek3.domain.service.unittest

import com.example.hhplusweek3.domain.model.Concert
import com.example.hhplusweek3.domain.model.ConcertSeat
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.ConcertService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.time.Instant

class ConcertServiceTest {
    private val mockConcertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private lateinit var concertService: ConcertService

    @BeforeEach
    fun setUp() {
        concertService =
            ConcertService(
                concertSeatRepository = mockConcertSeatRepository,
                reservationRepository = mockReservationRepository,
            )
    }

    @Test
    @DisplayName("예약 가능한 스케줄을 반환한다")
    fun `should return available schedules`() {
        // given
        val now = Instant.now()
        val seat1 = mockConcertSeat(dateUtc = now.plusSeconds(3600), seatNumber = 1L)
        val seat2 = mockConcertSeat(dateUtc = now.plusSeconds(7200), seatNumber = 2L)
        `when`(mockConcertSeatRepository.findAll()).thenReturn(listOf(seat1, seat2))

        `when`(mockReservationRepository.findAll()).thenReturn(emptyList())

        // when
        val result = concertService.getAvailableSchedules()

        // then
        val expectedSchedules =
            listOf(
                Concert.Schedule(date = seat1.dateUtc, seats = listOf(Concert.Schedule.Seat(number = 1L))),
                Concert.Schedule(date = seat2.dateUtc, seats = listOf(Concert.Schedule.Seat(number = 2L))),
            )

        assertEquals(expectedSchedules, result)

        verify(mockConcertSeatRepository).findAll()
        verify(mockReservationRepository).findAll()
    }

    @Test
    @DisplayName("예약된 좌석을 제외한 스케줄을 반환한다")
    fun `should return available schedules excluding reserved seats`() {
        // given
        val now = Instant.now()
        val seat1 = mockConcertSeat(dateUtc = now.plusSeconds(3600), seatNumber = 1L)
        val seat2 = mockConcertSeat(dateUtc = now.plusSeconds(7200), seatNumber = 2L)
        `when`(mockConcertSeatRepository.findAll()).thenReturn(listOf(seat1, seat2))

        val reservedSeat = mockReservation(seatNumber = 1L, dateUtc = seat1.dateUtc)
        `when`(mockReservationRepository.findAll()).thenReturn(listOf(reservedSeat))

        // when
        val result = concertService.getAvailableSchedules()

        // then
        val expectedSchedules =
            listOf(
                Concert.Schedule(date = seat2.dateUtc, seats = listOf(Concert.Schedule.Seat(number = 2L))),
            )

        assertEquals(expectedSchedules, result)

        verify(mockConcertSeatRepository).findAll()
        verify(mockReservationRepository).findAll()
    }

    @Test
    @DisplayName("예약 가능한 좌석을 반환한다")
    fun `should return available seats by date`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val seat1 = mockConcertSeat(dateUtc = date, seatNumber = 1L)
        val seat2 = mockConcertSeat(dateUtc = date, seatNumber = 2L)
        `when`(mockConcertSeatRepository.findByDate(date)).thenReturn(listOf(seat1, seat2))

        val reservedSeat = mockReservation(seatNumber = 1L, dateUtc = date)
        `when`(mockReservationRepository.findAllByDate(date)).thenReturn(listOf(reservedSeat))

        // when
        val result = concertService.getAvailableSeatsByDate(date)

        // then
        val expectedSeats =
            listOf(
                Concert.Schedule.Seat(number = 2L),
            )

        assertEquals(expectedSeats, result)

        verify(mockConcertSeatRepository).findByDate(date)
        verify(mockReservationRepository).findAllByDate(date)
    }

    @Test
    @DisplayName("해당 날짜의 모든 좌석을 반환한다")
    fun `should return all seats by date`() {
        // given
        val date = Instant.now().plusSeconds(3600)
        val seat1 = mockConcertSeat(dateUtc = date, seatNumber = 1L)
        val seat2 = mockConcertSeat(dateUtc = date, seatNumber = 2L)
        `when`(mockConcertSeatRepository.findByDate(date)).thenReturn(listOf(seat1, seat2))

        // when
        val result = concertService.getAllSeatsByDate(date)

        // then
        val expectedSeats =
            listOf(
                Concert.Schedule.Seat(number = 1L),
                Concert.Schedule.Seat(number = 2L),
            )

        assertEquals(expectedSeats, result)

        verify(mockConcertSeatRepository).findByDate(date)
        verifyNoInteractions(mockReservationRepository)
    }

    private fun mockConcertSeat(
        dateUtc: Instant,
        seatNumber: Long,
    ): ConcertSeat {
        val seat = mock(ConcertSeat::class.java)
        `when`(seat.dateUtc).thenReturn(dateUtc)
        `when`(seat.seatNumber).thenReturn(seatNumber)
        return seat
    }

    private fun mockReservation(
        seatNumber: Long,
        dateUtc: Instant,
    ): Reservation {
        val reservation = mock(Reservation::class.java)
        `when`(reservation.reservedSeat).thenReturn(seatNumber)
        `when`(reservation.dateTimeUtc).thenReturn(dateUtc)
        return reservation
    }
}
