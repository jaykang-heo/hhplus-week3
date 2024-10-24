package com.example.hhplusweek3.domain.service.unittest

import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.ReservationService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.time.Instant

class ReservationServiceTest {
    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val concertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val sut = ReservationService(mockReservationRepository, concertSeatRepository)

    @Test
    @DisplayName("예약이 존재하지 않으면, 아무 작업도 하지 않는다")
    fun `when no reservation exists, then do nothing`() {
        // given
        val date = Instant.parse("2023-10-01T00:00:00Z")
        val seatNumber = 1L

        `when`(mockReservationRepository.findBySeatNumberAndDate(seatNumber, date))
            .thenReturn(null)

        // when
        sut.deleteIfExpired(date, seatNumber)

        // then
        verify(mockReservationRepository).findBySeatNumberAndDate(seatNumber, date)
        verifyNoMoreInteractions(mockReservationRepository)
    }

    @Test
    @DisplayName("예약이 만료되지 않았으면, 삭제하지 않는다")
    fun `when reservation is not expired, then do not delete`() {
        // given
        val now = Instant.now()
        val date = now
        val seatNumber = 1L
        val reservation =
            Reservation(
                id = "reservation-id-1",
                paymentId = null,
                queueToken = "queue-token",
                dateTimeUtc = date,
                reservedSeat = seatNumber,
                amount = 100L,
                createdTimeUtc = now.minusSeconds(3600),
                updatedTimeUtc = now.minusSeconds(1800),
                expirationTimeUtc = now.plusSeconds(3600), // Expires in 1 hour
            )

        `when`(mockReservationRepository.findBySeatNumberAndDate(seatNumber, date))
            .thenReturn(reservation)

        // when
        sut.deleteIfExpired(date, seatNumber)

        // then
        verify(mockReservationRepository).findBySeatNumberAndDate(seatNumber, date)
        verifyNoMoreInteractions(mockReservationRepository)
    }

    @Test
    @DisplayName("예약이 만료되었으면, 예약을 삭제한다")
    fun `when reservation is expired, then delete reservation`() {
        // given
        val now = Instant.now()
        val date = now
        val seatNumber = 1L
        val reservation =
            Reservation(
                id = "reservation-id-2",
                paymentId = null,
                queueToken = "queue-token",
                dateTimeUtc = date,
                reservedSeat = seatNumber,
                amount = 100L,
                createdTimeUtc = now.minusSeconds(7200),
                updatedTimeUtc = now.minusSeconds(3600),
                expirationTimeUtc = now.minusSeconds(1800), // Expired 30 minutes ago
            )

        `when`(mockReservationRepository.findBySeatNumberAndDate(seatNumber, date))
            .thenReturn(reservation)

        // when
        sut.deleteIfExpired(date, seatNumber)

        // then
        verify(mockReservationRepository).findBySeatNumberAndDate(seatNumber, date)
        verify(mockReservationRepository).deleteByReservationId(reservation.id)
        verifyNoMoreInteractions(mockReservationRepository)
    }
}
