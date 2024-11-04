package com.example.hhplusweek3.domain.service.unittest

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.service.QueueService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class QueueServiceTest {
    private val queueRepository = mock(QueueRepository::class.java)
    private val sut = QueueService(queueRepository)

    @Test
    @DisplayName("generateQueue-대기열을 생성하면, 정상적으로 대기열을 생성한다")
    fun `when generate queue, then return queue`() {
        // when
        val result = sut.generateQueue()

        // then
        assertThat(result).isNotNull
        assertThat(result).isInstanceOf(Queue::class.java)
    }
}
