package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetQueueQuery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GetQueueQueryValidatorTest {
    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val sut = GetQueueQueryValidator(mockQueueRepository)

    @Test
    @DisplayName("대기열 토큰이 존재하지 않으면 QueueNotFoundException을 반환한다")
    fun `when queue does not exist, then throw QueueNotFoundException`() {
        // given
        val query = GetQueueQuery("non-existent-token")
        `when`(mockQueueRepository.findByToken(query.token)).thenReturn(null)

        // when & then
        assertThrows<QueueNotFoundException> {
            sut.validate(query)
        }
        verify(mockQueueRepository).findByToken("non-existent-token")
    }

    @Test
    @DisplayName("대기열 토큰이 존재하면 검증을 통과한다")
    fun `when queue exists, then validation passes`() {
        // given
        val query = GetQueueQuery("existing-token")
        val queue = mock(Queue::class.java)
        `when`(mockQueueRepository.findByToken("existing-token")).thenReturn(queue)

        // when & then
        assertDoesNotThrow {
            sut.validate(query)
        }
        verify(mockQueueRepository).findByToken("existing-token")
    }
}
