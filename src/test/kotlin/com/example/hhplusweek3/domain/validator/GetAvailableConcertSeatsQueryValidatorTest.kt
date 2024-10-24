package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.exception.ConcertDateNotFoundException
import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.Instant

class GetAvailableConcertSeatsQueryValidatorTest {
    private val mockConcertSeatRepository = mock(ConcertSeatRepository::class.java)
    private val sut = GetAvailableConcertSeatsQueryValidator(mockConcertSeatRepository)

    @Test
    @DisplayName("콘서트 좌석이 해당 날짜에 존재하지 않으면 ConcertDateNotFoundException을 반환한다")
    fun `when concert date does not exist, then throw ConcertDateNotFoundException`() {
        // given
        val dateUtc = Instant.now().plusSeconds(60)
        val query = FindAvailableConcertSeatsQuery(dateUtc)
        `when`(mockConcertSeatRepository.existsByDate(dateUtc)).thenReturn(false)

        // when & then
        assertThrows(ConcertDateNotFoundException::class.java) {
            sut.validate(query)
        }
        verify(mockConcertSeatRepository).existsByDate(dateUtc)
    }

    @Test
    @DisplayName("콘서트 좌석이 존재하면 검증을 통과한다")
    fun `when concert date exists, then validation passes`() {
        // given
        val dateUtc = Instant.now().plusSeconds(60)
        val query = FindAvailableConcertSeatsQuery(dateUtc)
        `when`(mockConcertSeatRepository.existsByDate(dateUtc)).thenReturn(true)

        // when & then
        assertDoesNotThrow {
            sut.validate(query)
        }
        verify(mockConcertSeatRepository).existsByDate(dateUtc)
    }
}
