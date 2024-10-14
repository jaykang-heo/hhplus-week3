package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.validator.ChargeWalletCommandValidator
import com.example.hhplusweek3.domain.validator.GetWalletBalanceQueryValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`

class WalletFacadeTest {

    private val mockWalletRepository = mock(WalletRepository::class.java)
    private val mockQueueService = mock(QueueService::class.java)
    private val mockChargeWalletCommandValidator = mock(ChargeWalletCommandValidator::class.java)
    private val mockGetWalletBalanceQueryValidator = mock(GetWalletBalanceQueryValidator::class.java)

    private val sut = WalletFacade(mockWalletRepository, mockQueueService, mockChargeWalletCommandValidator, mockGetWalletBalanceQueryValidator)

    @Test
    @DisplayName("대기열 선행 작업이 실패하면, 실행을 중단한다")
    fun `when queue pre-run fails, then stop`() {
        // given
        val command = ChargeWalletCommand(100L, "token")
        doThrow(RuntimeException("Queue pre-run failed")).`when`(mockQueueService).preRun("token")

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.charge(command)
        }

        verify(mockQueueService).preRun("token")
        verifyNoInteractions(mockChargeWalletCommandValidator, mockWalletRepository)
    }

    @Test
    @DisplayName("충전 명령 검증이 실패하면, 실행을 중단한다")
    fun `when command validation fails, then stop`() {
        // given
        val command = ChargeWalletCommand(100L, "token")
        doThrow(IllegalArgumentException("Invalid command")).`when`(mockChargeWalletCommandValidator).validate(command)

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.charge(command)
        }

        verify(mockQueueService).preRun("token")
        verify(mockChargeWalletCommandValidator).validate(command)
        verifyNoInteractions(mockWalletRepository)
    }

    @Test
    @DisplayName("충전이 성공하면, 충전된 지갑을 반환한다")
    fun `when charge succeeds, then return charged wallet`() {
        // given
        val command = ChargeWalletCommand(100L, "token")
        val chargedWallet = Wallet(100L)
        `when`(mockWalletRepository.charge(100L, "token")).thenReturn(chargedWallet)

        // when
        val result = sut.charge(command)

        // then
        assertEquals(chargedWallet, result)
        verify(mockQueueService).preRun("token")
        verify(mockChargeWalletCommandValidator).validate(command)
        verify(mockWalletRepository).charge(100L, "token")
    }

    @Test
    @DisplayName("대기열 선행 작업이 실패하면, 실행을 중단한다")
    fun `when get queue pre-run fails, then stop`() {
        // given
        val query = GetWalletBalanceQuery("token")
        doThrow(RuntimeException("Queue pre-run failed")).`when`(mockQueueService).preRun("token")

        // when & then
        assertThrows(RuntimeException::class.java) {
            sut.get(query)
        }

        verify(mockQueueService).preRun("token")
        verifyNoInteractions(mockGetWalletBalanceQueryValidator, mockWalletRepository)
    }

    @Test
    @DisplayName("쿼리 검증이 실패하면, 실행을 중단한다")
    fun `when query validation fails, then stop`() {
        // given
        val query = GetWalletBalanceQuery("token")
        doThrow(IllegalArgumentException("Invalid query")).`when`(mockGetWalletBalanceQueryValidator).validate(query)

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.get(query)
        }

        verify(mockQueueService).preRun("token")
        verify(mockGetWalletBalanceQueryValidator).validate(query)
        verifyNoInteractions(mockWalletRepository)
    }

    @Test
    @DisplayName("지갑 조회가 성공하면, 해당 지갑을 반환한다")
    fun `when wallet retrieval succeeds, then return the wallet`() {
        // given
        val query = GetWalletBalanceQuery("token")
        val expectedWallet = Wallet(1000L)
        `when`(mockWalletRepository.getByQueueToken("token")).thenReturn(expectedWallet)

        // when
        val result = sut.get(query)

        // then
        assertEquals(expectedWallet, result)
        verify(mockQueueService).preRun("token")
        verify(mockGetWalletBalanceQueryValidator).validate(query)
        verify(mockWalletRepository).getByQueueToken("token")
    }
}
