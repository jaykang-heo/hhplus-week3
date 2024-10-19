package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import com.example.hhplusweek3.domain.service.WalletService
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
    private val mockChargeWalletCommandValidator = mock(ChargeWalletCommandValidator::class.java)
    private val mockGetWalletBalanceQueryValidator = mock(GetWalletBalanceQueryValidator::class.java)
    private val mockWalletService = mock(WalletService::class.java)
    private val sut = WalletFacade(
        mockWalletService,
        mockChargeWalletCommandValidator,
        mockGetWalletBalanceQueryValidator,
        mockWalletRepository
    )

    @Test
    @DisplayName("충전 명령 검증이 실패하면, 실행을 중단한다")
    fun `when command validation fails, then stop`() {
        // given
        val command = ChargeWalletCommand(100L, "token")
        doThrow(IllegalArgumentException("Invalid command"))
            .`when`(mockChargeWalletCommandValidator).validate(command)

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.charge(command)
        }

        verify(mockChargeWalletCommandValidator).validate(command)
        verifyNoInteractions(mockWalletService)
        verifyNoInteractions(mockWalletRepository)
    }

    @Test
    @DisplayName("충전이 성공하면, 충전된 지갑을 반환한다")
    fun `when charge succeeds, then return charged wallet`() {
        // given
        val command = ChargeWalletCommand(100L, "token")
        val walletFromService = Wallet(100L, "token")
        val savedWallet = Wallet(100L, "token")

        `when`(mockWalletService.add(100L, "token")).thenReturn(walletFromService)
        `when`(mockWalletRepository.save(walletFromService)).thenReturn(savedWallet)

        // when
        val result = sut.charge(command)

        // then
        assertEquals(savedWallet, result)
        verify(mockChargeWalletCommandValidator).validate(command)
        verify(mockWalletService).add(100L, "token")
        verify(mockWalletRepository).save(walletFromService)
    }

    @Test
    @DisplayName("쿼리 검증이 실패하면, 실행을 중단한다")
    fun `when query validation fails, then stop`() {
        // given
        val query = GetWalletBalanceQuery("token")
        doThrow(IllegalArgumentException("Invalid query"))
            .`when`(mockGetWalletBalanceQueryValidator).validate(query)

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            sut.get(query)
        }

        verify(mockGetWalletBalanceQueryValidator).validate(query)
        verifyNoInteractions(mockWalletRepository)
    }

    @Test
    @DisplayName("지갑 조회가 성공하면, 해당 지갑을 반환한다")
    fun `when wallet retrieval succeeds, then return the wallet`() {
        // given
        val query = GetWalletBalanceQuery("token")
        val expectedWallet = Wallet(1000L, "token")
        `when`(mockWalletRepository.findByQueueToken("token")).thenReturn(expectedWallet)

        // when
        val result = sut.get(query)

        // then
        assertEquals(expectedWallet, result)
        verify(mockGetWalletBalanceQueryValidator).validate(query)
        verify(mockWalletRepository).findByQueueToken("token")
    }

    @Test
    @DisplayName("지갑이 없을 경우, 기본 지갑을 반환한다")
    fun `when wallet not found, then return default wallet`() {
        // given
        val query = GetWalletBalanceQuery("token")
        `when`(mockWalletRepository.findByQueueToken("token")).thenReturn(null)

        // when
        val result = sut.get(query)

        // then
        assertEquals(Wallet(0L, "token"), result)
        verify(mockGetWalletBalanceQueryValidator).validate(query)
        verify(mockWalletRepository).findByQueueToken("token")
    }
}
