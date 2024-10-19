package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetQueueQuery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GetQueueQueryValidatorTest {

    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val sut = GetQueueQueryValidator(mockQueueRepository)

    @Test
    @DisplayName("대기열 토큰이 존재하지 않는다면, 에러를 반환한다")
    fun `when find queue by token return null, then throw error`() {
        // given
        val query = GetQueueQuery("non-existent-token")
        `when`(mockQueueRepository.findByToken(query.token)).thenReturn(null)

        // when & then
        assertThrows<RuntimeException> {
            sut.validate(query)
        }.also {
            assert(it.message == "queue not found by non-existent-token")
        }

        verify(mockQueueRepository).findByToken("non-existent-token")
    }

    @Test
    @DisplayName("대기열 토큰이 존재한다면, 통과한다")
    fun `when queue token exists, then pass`() {
        // given
        val query = GetQueueQuery("existing-token")
        val queue = mock(Queue::class.java)
        `when`(mockQueueRepository.findByToken("existing-token")).thenReturn(queue)

        // when
        sut.validate(query)

        // then
        verify(mockQueueRepository).findByToken("existing-token")
    }
}
