package com.example.hhplusweek3.application.integrationtest.concurrency

import com.example.hhplusweek3.application.WalletFacade
import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import com.example.hhplusweek3.testservice.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WalletFacadeChargeConcurrencyIntegrationTest(
    @Autowired private val walletFacade: WalletFacade,
    @Autowired private val testUtils: TestUtils,
) {
    @Test
    fun `동시에 여러번 충전을 할때, 모두 정상적으로 충전된다`() {
        // given
        val threadCount = 10
        val amount = 1000L
        val expectedAmount = amount * threadCount
        testUtils.resetConcertSeats()
        val queueToken = testUtils.issueAndActivateQueueToken().token
        val initialBalance = walletFacade.get(GetWalletBalanceQuery(queueToken))
        assertThat(initialBalance.balance).isEqualTo(0)
        val commands = List(threadCount) { ChargeWalletCommand(amount, queueToken) }

        // when
        testUtils.asyncRun(threadCount, commands) { command ->
            walletFacade.charge(command)
        }

        // then
        val actualWallet = walletFacade.get(GetWalletBalanceQuery(queueToken))
        assertThat(actualWallet.balance).isEqualTo(expectedAmount)
    }
}
