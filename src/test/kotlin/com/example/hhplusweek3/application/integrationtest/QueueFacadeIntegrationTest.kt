package com.example.hhplusweek3.application.integrationtest

import com.example.hhplusweek3.application.QueueFacade
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.query.GetQueueQuery
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.testservice.TestService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.temporal.ChronoUnit

@SpringBootTest
@Transactional
class QueueFacadeIntegrationTest(
    @Autowired private val sut: QueueFacade,
    @Autowired private val testService: TestService
) {

    @BeforeEach
    fun setup() {
        testService.resetQueues()
    }

    @Test
    @DisplayName("대기열에 참가한다면 대기열에 참가하고 대기열 정보를 받는다")
    fun `when issue token then issue token and get queue response`() {
        // given
        val command = IssueQueueTokenCommand()

        // when
        val issuedQueue = sut.issue(command)

        // then
        assertThat(issuedQueue).isNotNull
        assertThat(issuedQueue.token).isNotBlank()
        assertThat(issuedQueue.status).isEqualTo(QueueStatus.ACTIVE)
        assertThat(issuedQueue.expirationTimeUtc).isAfter(issuedQueue.createdTimeUtc)
        val expectedExpiration = issuedQueue.createdTimeUtc.plusSeconds(60)
        assertThat(issuedQueue.expirationTimeUtc)
            .isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS))
    }

    @Test
    @DisplayName("대기열이 모두 찼으면 대기열을 1분 후 만료 설정하여 대기 상태로 생성한다")
    fun `when queue is full then create queue as pending`() {
        // given
        repeat(QueueService.ACTIVE_LIMIT) {
            val command = IssueQueueTokenCommand()
            val queue = sut.issue(command)
            testService.activateQueue(queue.token)
        }
        val activeQueues = testService.getActiveQueues()
        assertThat(activeQueues.size).isEqualTo(QueueService.ACTIVE_LIMIT)

        // when
        val command = IssueQueueTokenCommand()
        val newQueue = sut.issue(command)

        // then
        assertThat(newQueue).isNotNull
        assertThat(newQueue.status).isEqualTo(QueueStatus.PENDING)
        val expectedExpiration = newQueue.createdTimeUtc.plusSeconds(60)
        assertThat(newQueue.expirationTimeUtc)
            .isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS))
    }

    @Test
    @DisplayName("대기열 칸이 남아있다면 바로 활성화한다")
    fun `when queue is not full then create queue as active`() {
        // given
        val currentActive = testService.getActiveQueues().size
        QueueService.ACTIVE_LIMIT - currentActive

        // when
        val command = IssueQueueTokenCommand()
        val newQueue = sut.issue(command)

        // then
        assertThat(newQueue).isNotNull
        assertThat(newQueue.status).isEqualTo(QueueStatus.ACTIVE)
        val expectedExpiration = newQueue.createdTimeUtc.plusSeconds(60)
        assertThat(newQueue.expirationTimeUtc)
            .isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS))
    }

    @Test
    @DisplayName("대기열 정보를 조회할떄 대기열 토큰이 존재하지 않는다면, 에러를 반환한다")
    fun `when get queue info and queue token does not exist, then throw error`() {
        // given
        val invalidToken = "nonexistenttoken123"
        val query = GetQueueQuery(token = invalidToken)

        // when & then
        assertThatThrownBy { sut.get(query) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("queue not found by $invalidToken")
    }

    @Test
    @DisplayName("대기열을 조회할떄 대기열이 존재한다면 대기열 정보를 반환한다")
    fun `when get queue info and queue token exists, then return queue info`() {
        // given
        val command = IssueQueueTokenCommand()
        val issuedQueue = sut.issue(command)
        val query = GetQueueQuery(token = issuedQueue.token)

        // when
        val retrievedQueue = sut.get(query)

        // then
        assertThat(retrievedQueue).isNotNull
        assertThat(retrievedQueue.token).isEqualTo(issuedQueue.token)
        assertThat(retrievedQueue.status).isEqualTo(issuedQueue.status)
    }
}
