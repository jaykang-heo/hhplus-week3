package com.example.hhplusweek3.domain

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import java.time.Instant

class QueueServiceTest {

    private val queueRepository = mock(QueueRepository::class.java)
    private val sut = QueueService(queueRepository)

    @Test
    @DisplayName("대기열을 생성하면, 정상적으로 대기열을 생성한다")
    fun `when generate queue, then return queue`() {
        // given
        val command = IssueQueueTokenCommand()

        // when
        val result = sut.generateQueue(command)

        // then
        assertThat(result).isNotNull
        assertThat(result).isInstanceOf(Queue::class.java)
    }

    @Test
    @DisplayName("만료된 대기열들을 만료시킨다면, 만료한다")
    fun `when expire expired queues, then expire`() {
        // given
        val expirationTime = Instant.now()
        val expiredQueues = listOf(
            Queue(IssueQueueTokenCommand()),
            Queue(IssueQueueTokenCommand())
        )
        `when`(queueRepository.findAllByActiveAndBeforeTime(expirationTime)).thenReturn(expiredQueues)

        // when
        sut.expireBeforeTime(expirationTime)

        // then
        verify(queueRepository).findAllByActiveAndBeforeTime(expirationTime)
        verify(queueRepository).changeStatusToExpire(expiredQueues.map { it.token })
    }

    @Test
    @DisplayName("대기열을 찾는 함수가 에러를 반환한다면, 만료시키지 않는다")
    fun `when find expired queues fail, then do not expire queue`() {
        // given
        val expirationTime = Instant.now()
        `when`(queueRepository.findAllByActiveAndBeforeTime(expirationTime)).thenThrow(RuntimeException("Database error"))

        // when
        assertThrows<RuntimeException> { sut.expireBeforeTime(expirationTime) }

        // then
        verify(queueRepository).findAllByActiveAndBeforeTime(expirationTime)
        verify(queueRepository, never()).changeStatusToExpire(any())
    }

    @Test
    @DisplayName("대기열 상태를 변화하는게 실패하면, 상태를 변환하지 않는다")
    fun `when change status fails, then do nothing`() {
        // given
        val expirationTime = Instant.now()
        val expiredQueues = listOf(
            Queue(IssueQueueTokenCommand())
        )
        `when`(queueRepository.findAllByActiveAndBeforeTime(expirationTime)).thenReturn(expiredQueues)
        `when`(queueRepository.changeStatusToExpire(any())).thenThrow(RuntimeException("Database error"))

        // when
        assertThrows<RuntimeException> { sut.expireBeforeTime(expirationTime) }

        // then
        verify(queueRepository).findAllByActiveAndBeforeTime(expirationTime)
        verify(queueRepository).changeStatusToExpire(expiredQueues.map { it.token })
    }
}
