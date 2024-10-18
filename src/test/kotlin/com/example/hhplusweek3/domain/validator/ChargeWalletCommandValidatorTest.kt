package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Instant

class ChargeWalletCommandValidatorTest {

    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val sut = ChargeWalletCommandValidator(mockQueueRepository)

    @Test
    @DisplayName("큐가 존재하지 않으면, 에러를 반환한다")
    fun `when queue does not exist, then throw error`() {
        // given
        val command = ChargeWalletCommand(100L, "non-existent-token")
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("queue not found"))
    }

    @Test
    @DisplayName("큐가 활성 상태가 아니면, 에러를 반환한다")
    fun `when queue is not active, then throw error`() {
        // given
        val command = ChargeWalletCommand(100L, "inactive-token")
        val inactiveQueue = Queue("inactive-token", QueueStatus.PENDING, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(command)
        }
        assert(exception.message!!.contains("queue is not active"))
    }

    @Test
    @DisplayName("큐가 존재하고 활성 상태이면, 검증을 통과한다")
    fun `when queue exists and is active, then validation passes`() {
        // given
        val command = ChargeWalletCommand(100L, "active-token")
        val activeQueue = Queue("active-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("active-token")).thenReturn(activeQueue)

        // when & then
        assertDoesNotThrow {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("충전 금액이 0 이하이면, 에러를 반환한다")
    fun `when charge amount is not positive, then throw error`() {
        // given
        val command = ChargeWalletCommand(0L, "token")

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.validate(command)
        }
    }

    @Test
    @DisplayName("토큰이 비어있으면, 에러를 반환한다")
    fun `when token is empty, then throw error`() {
        // given
        val command = ChargeWalletCommand(100L, "")

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.validate(command)
        }
    }
}
