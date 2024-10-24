package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.exception.InvalidQueueStatusException
import com.example.hhplusweek3.domain.model.exception.QueueNotFoundException
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Instant

class GetWalletBalanceQueryValidatorTest {
    private val mockQueueRepository = mock(QueueRepository::class.java)
    private val sut = GetWalletBalanceQueryValidator(mockQueueRepository)

    @Test
    @DisplayName("쿼리 토큰이 비어있으면 IllegalArgumentException을 반환한다")
    fun `when query token is blank, then throw IllegalArgumentException`() {
        // given
        val query = GetWalletBalanceQuery(" ")

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.validate(query)
        }
    }

    @Test
    @DisplayName("큐가 존재하지 않으면 QueueNotFoundException을 반환한다")
    fun `when queue does not exist, then throw QueueNotFoundException`() {
        // given
        val query = GetWalletBalanceQuery("non-existent-token")
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        assertThrows(QueueNotFoundException::class.java) {
            sut.validate(query)
        }
    }

    @Test
    @DisplayName("큐가 활성 상태가 아니면 InvalidQueueStatusException을 반환한다")
    fun `when queue is not active, then throw InvalidQueueStatusException`() {
        // given
        val query = GetWalletBalanceQuery("inactive-token")
        val inactiveQueue =
            Queue(
                token = "inactive-token",
                status = QueueStatus.PENDING,
                createdTimeUtc = Instant.now(),
                updatedTimeUtc = Instant.now(),
                expirationTimeUtc = Instant.now().plusSeconds(3600),
            )
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        assertThrows(InvalidQueueStatusException::class.java) {
            sut.validate(query)
        }
    }

    @Test
    @DisplayName("큐가 존재하고 활성 상태이면 검증을 통과한다")
    fun `when queue exists and is active, then validation passes`() {
        // given
        val query = GetWalletBalanceQuery("valid-token")
        val activeQueue =
            Queue(
                token = "valid-token",
                status = QueueStatus.ACTIVE,
                createdTimeUtc = Instant.now(),
                updatedTimeUtc = Instant.now(),
                expirationTimeUtc = Instant.now().plusSeconds(3600),
            )
        `when`(mockQueueRepository.findByToken("valid-token")).thenReturn(activeQueue)

        // when & then
        assertDoesNotThrow {
            sut.validate(query)
        }
    }
}
