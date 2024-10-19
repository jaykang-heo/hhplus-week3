package com.example.hhplusweek3.application.unittest

import com.example.hhplusweek3.application.QueueFacade
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetQueueQuery
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.validator.GetQueueQueryValidator
import com.example.hhplusweek3.domain.validator.IssueQueueTokenCommandValidator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class QueueFacadeTest {

    private lateinit var mockIssueQueueTokenCommandValidator: IssueQueueTokenCommandValidator
    private lateinit var mockQueueRepository: QueueRepository
    private lateinit var mockQueueService: QueueService
    private lateinit var mockGetQueueQueryValidator: GetQueueQueryValidator
    private lateinit var sut: QueueFacade

    @BeforeEach
    fun setup() {
        mockIssueQueueTokenCommandValidator = mock(IssueQueueTokenCommandValidator::class.java)
        mockQueueRepository = mock(QueueRepository::class.java)
        mockQueueService = mock(QueueService::class.java)
        mockGetQueueQueryValidator = mock(GetQueueQueryValidator::class.java)
        sut = QueueFacade(
            queueService = mockQueueService,
            queueRepository = mockQueueRepository,
            issueQueueTokenCommandValidator = mockIssueQueueTokenCommandValidator,
            getQueueQueryValidator = mockGetQueueQueryValidator
        )
    }

    @Test
    @DisplayName("대기열 토큰 발급 명령을 내리면, 대기열을 반환한다")
    fun `when issue queue token command, then return queue`() {
        // given
        val command = IssueQueueTokenCommand(/* initialize with necessary parameters if any */)
        val generatedQueue = Queue(command)
        val savedQueue = generatedQueue.copy() // Simulate any changes during save if necessary

        // Mock the generateQueue method to return the generatedQueue
        `when`(mockQueueService.generateQueue(command)).thenReturn(generatedQueue)

        // Mock the validate method to do nothing (i.e., pass validation)
        doNothing().`when`(mockIssueQueueTokenCommandValidator).validate(command)

        // Mock the save method to return the savedQueue
        `when`(mockQueueRepository.save(generatedQueue)).thenReturn(generatedQueue)

        // Mock the activatePendingQueues method to do nothing
        doNothing().`when`(mockQueueService).activatePendingQueues()

        // Mock the getByToken method to return the savedQueue
        `when`(mockQueueRepository.getByToken(generatedQueue.token)).thenReturn(savedQueue)

        // when
        val actual = sut.issue(command)

        // then
        assertThat(actual).isEqualTo(savedQueue)

        // Verify the interactions
        verify(mockQueueService).generateQueue(command)
        verify(mockIssueQueueTokenCommandValidator).validate(command)
        verify(mockQueueRepository).save(generatedQueue)
        verify(mockQueueService).activatePendingQueues()
        verify(mockQueueRepository).getByToken(generatedQueue.token)
    }

    @Test
    @DisplayName("대기열 검증 작업이 실패하면, 대기열을 반환하지 않는다")
    fun `when validate queue fail, then do not return queue`() {
        // given
        val command = IssueQueueTokenCommand(/* initialize with necessary parameters if any */)
        val generatedQueue = Queue(command)

        // Mock the generateQueue method to return the generatedQueue
        `when`(mockQueueService.generateQueue(command)).thenReturn(generatedQueue)

        // Mock the validate method to throw an exception
        doThrow(IllegalArgumentException("Validation failed"))
            .`when`(mockIssueQueueTokenCommandValidator).validate(command)

        // when & then
        assertThatThrownBy { sut.issue(command) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Validation failed")

        // Verify the interactions
        verify(mockQueueService).generateQueue(command)
        verify(mockIssueQueueTokenCommandValidator).validate(command)
        verify(mockQueueRepository, never()).save(any())
        verify(mockQueueService, never()).activatePendingQueues()
        verify(mockQueueRepository, never()).getByToken(any())
    }

    @Test
    @DisplayName("대기열 저장 작업이 실패하면, 대기열을 반환하지 않는다")
    fun `when save queue fail, then do not return queue`() {
        // given
        val command = IssueQueueTokenCommand(/* initialize with necessary parameters if any */)
        val generatedQueue = Queue(command)

        // Mock the generateQueue method to return the generatedQueue
        `when`(mockQueueService.generateQueue(command)).thenReturn(generatedQueue)

        // Mock the validate method to do nothing (i.e., pass validation)
        doNothing().`when`(mockIssueQueueTokenCommandValidator).validate(command)

        // Mock the save method to throw an exception
        `when`(mockQueueRepository.save(generatedQueue)).thenThrow(RuntimeException("Save failed"))

        // when & then
        assertThatThrownBy { sut.issue(command) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Save failed")

        // Verify the interactions
        verify(mockQueueService).generateQueue(command)
        verify(mockIssueQueueTokenCommandValidator).validate(command)
        verify(mockQueueRepository).save(generatedQueue)
        verify(mockQueueService, never()).activatePendingQueues()
        verify(mockQueueRepository, never()).getByToken(any())
    }

    @Test
    @DisplayName("대기열 정책 검증이 실패하면, 후속 작업을 실행하지 않는다")
    fun `when validate policy for get queue fail, then do not run other functions`() {
        // given
        val query = GetQueueQuery("token123")

        // Mock the validate method to throw an exception
        doThrow(IllegalArgumentException("Policy validation failed"))
            .`when`(mockGetQueueQueryValidator).validate(query)

        // when & then
        assertThatThrownBy { sut.get(query) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Policy validation failed")

        // Verify the interactions
        verify(mockGetQueueQueryValidator).validate(query)
        verify(mockQueueRepository, never()).getByToken(any())
    }

    @Test
    @DisplayName("대기열 조회에 성공하면, 대기열 정보를 반환한다")
    fun `when get queue info succeed, then return queue`() {
        // given
        val query = GetQueueQuery("token123")
        val queue = Queue(IssueQueueTokenCommand(/* initialize with necessary parameters if any */))

        // Mock the validate method to do nothing (i.e., pass validation)
        doNothing().`when`(mockGetQueueQueryValidator).validate(query)

        // Mock the getByToken method to return the queue
        `when`(mockQueueRepository.getByToken("token123")).thenReturn(queue)

        // when
        val result = sut.get(query)

        // then
        assertThat(result).isEqualTo(queue)

        // Verify the interactions
        verify(mockGetQueueQueryValidator).validate(query)
        verify(mockQueueRepository).getByToken("token123")
    }
}
