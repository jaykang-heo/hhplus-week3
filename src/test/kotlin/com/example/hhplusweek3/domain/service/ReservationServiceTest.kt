package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ReservationRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID

class ReservationServiceTest {

    private val mockReservationRepository = mock(ReservationRepository::class.java)
    private val sut = ReservationService(mockReservationRepository)

    @Test
    @DisplayName("만료된 예약이 존재하지 않는다면, 예약 삭제를 실행하지 않는다")
    fun `when there are no expired reservations, then do not run delete reservations`() {
        // given
        val now = Instant.now()
        `when`(mockReservationRepository.findAllByOrderNumberIsNullAndBeforeDate(any())).thenReturn(emptyList())

        // when
        sut.preRun()

        // then
        verify(mockReservationRepository).findAllByOrderNumberIsNullAndBeforeDate(any())
        verify(mockReservationRepository, never()).deleteAllByReservationIds(any())
    }

    @Test
    @DisplayName("만료된 예약이 존재한다면, 예약 삭제를 실행한다")
    fun `when there are expired reservations, then run delete reservations`() {
        // given
        val now = Instant.now()
        val expiredReservation1 = Reservation(
            reservationId = UUID.randomUUID().toString(),
            orderNumber = null,
            queueToken = "token1",
            dateTimeUtc = now.minusSeconds(3600),
            reservedSeat = 1L,
            createdTimeUtc = now.minusSeconds(7200),
            updatedTimeUtc = now.minusSeconds(7200),
            expirationTimeUtc = now.minusSeconds(3600)
        )
        val expiredReservation2 = Reservation(
            reservationId = UUID.randomUUID().toString(),
            orderNumber = null,
            queueToken = "token2",
            dateTimeUtc = now.minusSeconds(3600),
            reservedSeat = 2L,
            createdTimeUtc = now.minusSeconds(7200),
            updatedTimeUtc = now.minusSeconds(7200),
            expirationTimeUtc = now.minusSeconds(3600)
        )
        val expiredReservations = listOf(expiredReservation1, expiredReservation2)

        `when`(mockReservationRepository.findAllByOrderNumberIsNullAndBeforeDate(any())).thenReturn(expiredReservations)

        // when
        sut.preRun()

        // then
        verify(mockReservationRepository).findAllByOrderNumberIsNullAndBeforeDate(any())
        verify(mockReservationRepository).deleteAllByReservationIds(listOf(expiredReservation1.reservationId, expiredReservation2.reservationId))
    }
}
