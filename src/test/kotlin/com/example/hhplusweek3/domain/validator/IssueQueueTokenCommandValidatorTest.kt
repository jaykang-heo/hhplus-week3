package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.QueueLimit
import com.example.hhplusweek3.domain.model.exception.QueueLimitExceededException
import com.example.hhplusweek3.domain.port.QueueLimitRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class IssueQueueTokenCommandValidatorTest {
    private val queueLimitRepository = mock(QueueLimitRepository::class.java)
    private val sut = IssueQueueTokenCommandValidator(queueLimitRepository)

    @Test
    @DisplayName("활성화된 대기열 갯수가 대기열 활성 갯수 제한과 같으면 QueueLimitExceededException을 반환한다")
    fun `when issue count equals issue limit, then throw QueueLimitExceededException`() {
        // given
        val invalidQueueLimit = QueueLimit(10, 10)
        `when`(queueLimitRepository.getQueueLimit()).thenReturn(invalidQueueLimit)
        val command = IssueQueueTokenCommand()

        // when & then
        assertThrows<QueueLimitExceededException> {
            sut.validate(command)
        }
        verify(queueLimitRepository).getQueueLimit()
    }

    @Test
    @DisplayName("활성화된 대기열 갯수가 대기열 활성 갯수 제한보다 크면 QueueLimitExceededException을 반환한다")
    fun `when issue count exceeds issue limit, then throw QueueLimitExceededException`() {
        // given
        val invalidQueueLimit = QueueLimit(11, 10)
        `when`(queueLimitRepository.getQueueLimit()).thenReturn(invalidQueueLimit)
        val command = IssueQueueTokenCommand()

        // when & then
        assertThrows<QueueLimitExceededException> {
            sut.validate(command)
        }
        verify(queueLimitRepository).getQueueLimit()
    }

    @Test
    @DisplayName("활성화된 대기열 갯수가 대기열 제한보다 작으면 검증을 통과한다")
    fun `when issue count is less than issue limit, then validation passes`() {
        // given
        val validQueueLimit = QueueLimit(9, 10)
        `when`(queueLimitRepository.getQueueLimit()).thenReturn(validQueueLimit)
        val command = IssueQueueTokenCommand()

        // when & then
        assertDoesNotThrow {
            sut.validate(command)
        }
        verify(queueLimitRepository).getQueueLimit()
    }
}
