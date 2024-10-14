package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Wallet
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.validator.ChargeWalletCommandValidator
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
    private val sut = WalletFacade(mockWalletRepository, mockQueueService, mockChargeWalletCommandValidator)

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
}
