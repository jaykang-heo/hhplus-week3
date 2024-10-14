package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.WalletRepository
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
    private val mockWalletRepository = mock(WalletRepository::class.java)
    private val sut = GetWalletBalanceQueryValidator(mockQueueRepository, mockWalletRepository)

    @Test
    @DisplayName("쿼리 토큰이 비어있으면, 에러를 반환한다")
    fun `when query token is blank, then throw error`() {
        // given
        val query = GetWalletBalanceQuery(" ")

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.validate(query)
        }
    }

    @Test
    @DisplayName("큐가 존재하지 않으면, 에러를 반환한다")
    fun `when queue does not exist, then throw error`() {
        // given
        val query = GetWalletBalanceQuery("non-existent-token")
        `when`(mockQueueRepository.findByToken("non-existent-token")).thenReturn(null)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(query)
        }
        assert(exception.message!!.contains("Queue not found"))
    }

    @Test
    @DisplayName("큐가 활성 상태가 아니면, 에러를 반환한다")
    fun `when queue is not active, then throw error`() {
        // given
        val query = GetWalletBalanceQuery("inactive-token")
        val inactiveQueue = Queue("inactive-token", QueueStatus.PENDING, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("inactive-token")).thenReturn(inactiveQueue)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(query)
        }
        assert(exception.message!!.contains("Queue status is not active"))
    }

    @Test
    @DisplayName("지갑이 존재하지 않으면, 에러를 반환한다")
    fun `when wallet does not exist, then throw error`() {
        // given
        val query = GetWalletBalanceQuery("valid-token")
        val activeQueue = Queue("valid-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        `when`(mockQueueRepository.findByToken("valid-token")).thenReturn(activeQueue)
        `when`(mockWalletRepository.findByQueueToken("valid-token")).thenReturn(null)

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            sut.validate(query)
        }
        assert(exception.message!!.contains("Wallet not found"))
    }

    @Test
    @DisplayName("큐가 존재하고 활성 상태이며 지갑이 존재하면, 검증을 통과한다")
    fun `when queue exists, is active, and wallet exists, then validation passes`() {
        // given
        val query = GetWalletBalanceQuery("valid-token")
        val activeQueue = Queue("valid-token", QueueStatus.ACTIVE, Instant.now(), Instant.now(), Instant.now())
        val wallet = Wallet(1000L)
        `when`(mockQueueRepository.findByToken("valid-token")).thenReturn(activeQueue)
        `when`(mockWalletRepository.findByQueueToken("valid-token")).thenReturn(wallet)

        // when & then
        assertDoesNotThrow {
            sut.validate(query)
        }
    }
}
