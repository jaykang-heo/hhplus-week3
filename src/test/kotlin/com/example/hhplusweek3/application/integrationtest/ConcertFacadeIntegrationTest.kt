package com.example.hhplusweek3.application.integrationtest

import com.example.hhplusweek3.application.ConcertFacade
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import com.example.hhplusweek3.testservice.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest
class ConcertFacadeIntegrationTest(
    @Autowired private val sut: ConcertFacade,
    @Autowired private val testUtils: TestUtils,
) {
    @Test
    @DisplayName("예약 가능한 콘서트 좌석을 조회할때 콘서트 좌석이 모두 예약 가능하다면, 모든 50개 좌석이 반환된다")
    fun `when find available seats and all concert seats are available, then return all 50 seats`() {
        // given
        testUtils.resetConcertSeats()
        val date =
            LocalDate
                .now()
                .plusDays(2)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
        val query = FindAvailableConcertSeatsQuery(date)

        // when
        val actual = sut.findAvailableSeats(query)

        // then
        assertThat(
            actual.availableSchedules
                .first()
                .seats.size,
        ).isEqualTo(50)
        assertThat(
            actual.allSchedules
                .first()
                .seats.size,
        ).isEqualTo(50)
    }

    @Test
    @DisplayName("예약 가능한 콘서트 좌석을 조회할때 부분적으로 가능하다면, 예약되지 않은 콘서트 좌석만 반환한다")
    fun `when find available seats and partial seats are available, then return partial seats`() {
        // given
        val date =
            LocalDate
                .now()
                .plusDays(2)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
        testUtils.resetConcertSeats()
        testUtils.resetAndReserveHalfSeatsInDate(date)
        val query = FindAvailableConcertSeatsQuery(date)

        // when
        val actual = sut.findAvailableSeats(query)

        // then
        assertThat(
            actual.availableSchedules
                .first()
                .seats.size,
        ).isEqualTo(25)
        assertThat(
            actual.allSchedules
                .first()
                .seats.size,
        ).isEqualTo(50)
    }

    @Test
    @DisplayName("예약 가능한 콘서트 좌석을 예약할때 예약 가능한 좌석이 없으면, 빈 리스트를 반환한다")
    fun `when finding available seats and no seats are available, then empty list`() {
        // given
        val date =
            LocalDate
                .now()
                .plusDays(2)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
        testUtils.resetConcertSeats()
        testUtils.resetAndReserveAllSeatsInDate(date)
        val query = FindAvailableConcertSeatsQuery(date)

        // when
        val actual = sut.findAvailableSeats(query)

        // then
        assertThat(actual.availableSchedules.first().seats).isEmpty()
        assertThat(
            actual.allSchedules
                .first()
                .seats.size,
        ).isEqualTo(50)
    }

    @Test
    @DisplayName("예약 가능한 콘서트 좌석을 찾을때 존재하지 않는 날짜로 조회하면, 에러를 반환한다")
    fun `when finding available seats and date is not valid, then throw error`() {
        // given
        val date = Instant.now().plusSeconds(60)
        val query = FindAvailableConcertSeatsQuery(date)

        // when, then
        val actual = assertThrows<RuntimeException> { sut.findAvailableSeats(query) }
        assertThat(actual.message?.contains("does not exist")).isTrue()
    }

    @Test
    @DisplayName("예약 가능한 날짜를 찾을때 모든 날짜들이 예약 가능하다면, 모든 날짜를 반환한다")
    fun `when finding available dates and all dates are available, then return all dates`() {
        // given
        testUtils.resetConcertSeats()

        // when
        val actual = sut.findAvailableDates()

        // then
        assertThat(actual.availableSchedules.size).isEqualTo(10)
        assertThat(actual.allSchedules.size).isEqualTo(10)
    }

    @Test
    @DisplayName("예약 가능한 날짜를 찾을때 부분적으로 예약 가능하다면, 예약 가능한 날짜들만 반환한다")
    fun `when finding available dates and partial dates are available, then return partial dates as available`() {
        // given
        val date =
            LocalDate
                .now()
                .plusDays(2)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
        testUtils.resetConcertSeats()
        testUtils.resetAndReserveAllSeatsInDate(date)

        // when
        val actual = sut.findAvailableDates()

        // then
        assertThat(actual.availableSchedules.size).isEqualTo(9)
        assertThat(actual.allSchedules.size).isEqualTo(10)
    }

    @Test
    @DisplayName("예약 가능한 날짜를 찾을때 과거 날짜가 포함되지 않는다")
    fun `when find available dates, past date is not included`() {
        // given
        testUtils.resetConcertSeats(-5, -1)

        // when
        val actual = sut.findAvailableDates()

        // then
        assertThat(actual.availableSchedules).isEmpty()
        assertThat(actual.allSchedules.size).isEqualTo(5)
    }
}
