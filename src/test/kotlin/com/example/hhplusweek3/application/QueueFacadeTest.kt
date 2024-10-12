package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.IssueQueueTokenCommandValidator
import com.example.hhplusweek3.domain.QueueService
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class QueueFacadeTest {

    private lateinit var issueQueueTokenCommandValidator: IssueQueueTokenCommandValidator
    private lateinit var mockQueueRepository: QueueRepository
    private lateinit var mockQueueService: QueueService
    private lateinit var sut: QueueFacade

    @BeforeEach
    fun setup() {
        issueQueueTokenCommandValidator = mock(IssueQueueTokenCommandValidator::class.java)
        mockQueueRepository = mock(QueueRepository::class.java)
        mockQueueService = mock(QueueService::class.java)
        sut = QueueFacade(mockQueueService, mockQueueRepository, issueQueueTokenCommandValidator)
    }

    @Test
    @DisplayName("대기열 토큰 발급 명령을 내리면, 대기열을 반환한다")
    fun `when issue queue token command, then return queue`() {
        // given
        val command = IssueQueueTokenCommand()
        val queue = Queue(command)
        `when`(mockQueueService.generateQueue(command)).thenReturn(queue)
        doNothing().`when`(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        doNothing().`when`(issueQueueTokenCommandValidator).validate(command)
        `when`(mockQueueRepository.save(queue)).thenReturn(queue)

        // when
        val actual = sut.issue(command)

        // then
        assertThat(actual).isEqualTo(queue)
        verify(mockQueueService).generateQueue(command)
        verify(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        verify(issueQueueTokenCommandValidator).validate(command)
        verify(mockQueueRepository).save(queue)
    }

    @Test
    @DisplayName("대기열 만료 작업이 실패하면, 대기열을 반환하지 않는다")
    fun `when expire queue fail, then do not return queue`() {
        // given
        val command = IssueQueueTokenCommand()
        val queue = Queue(command)
        `when`(mockQueueService.generateQueue(command)).thenReturn(queue)
        doThrow(RuntimeException("Expire failed")).`when`(mockQueueService).expireBeforeTime(queue.createdTimeUtc)

        // when & then
        assertThatThrownBy { sut.issue(command) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Expire failed")

        verify(mockQueueService).generateQueue(command)
        verify(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        verify(issueQueueTokenCommandValidator, never()).validate(command)
        verify(mockQueueRepository, never()).save(queue)
    }

    @Test
    @DisplayName("대기열 검증 작업이 실패하면, 대기열을 반환하지 않는다")
    fun `when validate queue fail, then do not return queue`() {
        // given
        val command = IssueQueueTokenCommand()
        val queue = Queue(command)
        `when`(mockQueueService.generateQueue(command)).thenReturn(queue)
        doNothing().`when`(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        doThrow(IllegalArgumentException("Validation failed")).`when`(issueQueueTokenCommandValidator).validate(command)

        // when & then
        assertThatThrownBy { sut.issue(command) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Validation failed")

        verify(mockQueueService).generateQueue(command)
        verify(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        verify(issueQueueTokenCommandValidator).validate(command)
        verify(mockQueueRepository, never()).save(queue)
    }

    @Test
    @DisplayName("대기열 저장 작업이 실패하면, 대기열을 반환하지 않는다")
    fun `when save queue fail, then do not return queue`() {
        // given
        val command = IssueQueueTokenCommand()
        val queue = Queue(command)
        `when`(mockQueueService.generateQueue(command)).thenReturn(queue)
        doNothing().`when`(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        doNothing().`when`(issueQueueTokenCommandValidator).validate(command)
        `when`(mockQueueRepository.save(queue)).thenThrow(RuntimeException("Save failed"))

        // when & then
        assertThatThrownBy { sut.issue(command) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Save failed")

        verify(mockQueueService).generateQueue(command)
        verify(mockQueueService).expireBeforeTime(queue.createdTimeUtc)
        verify(issueQueueTokenCommandValidator).validate(command)
        verify(mockQueueRepository).save(queue)
    }
}
