package com.example.hhplusweek3.application.integrationtest.concurrency

import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.config.IntegrationTest
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.testservice.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.ZoneOffset

@IntegrationTest
class ReservationFacadeReserveConcurrencyIntegrationTest(
    @Autowired private val reservationFacade: ReservationFacade,
    @Autowired private val reservationRepository: ReservationRepository,
    @Autowired private val testUtils: TestUtils,
) {
    @Test
    fun `동시에 같은 예약을 여러번 할때, 예약은 한번만 된다`() {
        // given
        testUtils.resetConcertSeats()
        testUtils.resetDatabase()
        val count = 10
        val concertDate =
            LocalDate
                .now()
                .atStartOfDay()
                .plusDays(2)
                .toInstant(ZoneOffset.UTC)
        val seatNumber = 2L
        val queueToken = testUtils.issueQueueToken()
        val commands = (1..10).map { CreateReservationCommand(queueToken, seatNumber, concertDate) }

        // when
        testUtils.asyncRun(count, commands) { command ->
            reservationFacade.reserve(command)
        }

        // then
        val actualReservations =
            reservationRepository.findAll().map {
                it.queueToken == queueToken && it.reservedSeat == seatNumber && it.dateTimeUtc == concertDate
            }
        assertThat(actualReservations).hasSize(1)
    }

    @Test
    fun `여러명이 동시에 같은 날짜의 좌석을 예약 시도하면, 한명만 성공한다`() {
        testUtils.resetConcertSeats()
        testUtils.resetDatabase()
        val count = 10
        val concertDate =
            LocalDate
                .now()
                .atStartOfDay()
                .plusDays(2)
                .toInstant(ZoneOffset.UTC)
        val seatNumber = 2L
        val queueTokens = (1..count).map { testUtils.issueQueueToken() }
        val commands = queueTokens.map { queueToken -> CreateReservationCommand(queueToken, seatNumber, concertDate) }

        // when
        testUtils.asyncRun(count, commands) { command ->
            reservationFacade.reserve(command)
        }

        // then
        val actualReservations =
            reservationRepository.findAll().map {
                queueTokens.contains(it.queueToken) && it.reservedSeat == seatNumber && it.dateTimeUtc == concertDate
            }
        assertThat(actualReservations).hasSize(1)
    }
}
