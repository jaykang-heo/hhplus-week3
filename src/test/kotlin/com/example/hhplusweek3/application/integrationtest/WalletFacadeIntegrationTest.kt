package com.example.hhplusweek3.application.integrationtest

import com.example.hhplusweek3.application.WalletFacade
import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.WalletRepository
import com.example.hhplusweek3.domain.query.GetWalletBalanceQuery
import com.example.hhplusweek3.testservice.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class WalletFacadeIntegrationTest(
    @Autowired private val sut: WalletFacade,
    @Autowired private val testUtils: TestUtils,
    @Autowired private val walletRepository: WalletRepository,
    @Autowired private val queueRepository: QueueRepository,
) {
    private lateinit var activeQueue: Queue

    @BeforeEach
    fun setup() {
        testUtils.resetQueues()
        testUtils.resetWallets()
        activeQueue = testUtils.issueAndActivateQueueToken()
    }

    @Test
    @DisplayName("지갑을 충전할떄 토큰이 활성화 되어 있지 않다면, 에러를 반환한다")
    fun `when charge wallet and queue token is not active, then throw error`() {
        val nonActiveQueue = activeQueue.copy(status = QueueStatus.PENDING)
        queueRepository.update(nonActiveQueue)

        val chargeCommand =
            ChargeWalletCommand(
                queueToken = nonActiveQueue.token,
                amount = 1000L,
            )

        assertThatThrownBy { sut.charge(chargeCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("queue is not active")
    }

    @Test
    @DisplayName("지갑을 충전할떄 토큰이 존재하지 않는다면, 에러를 반환한다")
    fun `when charge wallet and queue token does not exist, then throw error`() {
        val invalidToken = "invalid-token-123"
        val chargeCommand =
            ChargeWalletCommand(
                queueToken = invalidToken,
                amount = 1000L,
            )

        assertThatThrownBy { sut.charge(chargeCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("queue not found by $invalidToken")
    }

    @Test
    @DisplayName("지갑을 충전할떄 요청이 정상적이라면, 성공한다")
    fun `when charge wallet and command is valid, then succeed`() {
        val chargeCommand =
            ChargeWalletCommand(
                queueToken = activeQueue.token,
                amount = 5000L,
            )

        val wallet = sut.charge(chargeCommand)

        assertThat(wallet).isNotNull
        assertThat(wallet.balance).isEqualTo(5000L)
        assertThat(wallet.queueToken).isEqualTo(activeQueue.token)
    }

    @Test
    @DisplayName("지갑을 충전할떄 처음으로 충전한다면, 지갑을 자동 생성하고 저장한다")
    fun `when charge wallet and it is first time, then auto generate wallet and save`() {
        val newQueueToken = testUtils.issueAndActivateQueueToken().token
        val chargeCommand =
            ChargeWalletCommand(
                queueToken = newQueueToken,
                amount = 3000L,
            )

        val wallet = sut.charge(chargeCommand)

        assertThat(wallet).isNotNull
        assertThat(wallet.balance).isEqualTo(3000L)
        assertThat(wallet.queueToken).isEqualTo(newQueueToken)

        val savedWallet = walletRepository.findByQueueToken(newQueueToken)
        assertThat(savedWallet).isNotNull
        assertThat(savedWallet!!.balance).isEqualTo(3000L)
    }

    @Test
    @DisplayName("지갑을 충전할떄 지갑이 존재한다면, 존재하는 지갑에 더하고, 업데이트된 지갑을 저장한다")
    fun `when charge wallet and there already is a wallet, then add amount and update the wallet`() {
        val initialChargeCommand =
            ChargeWalletCommand(
                queueToken = activeQueue.token,
                amount = 2000L,
            )
        sut.charge(initialChargeCommand)

        val secondChargeCommand =
            ChargeWalletCommand(
                queueToken = activeQueue.token,
                amount = 3000L,
            )
        val updatedWallet = sut.charge(secondChargeCommand)

        assertThat(updatedWallet.balance).isEqualTo(5000L)

        val savedWallet = walletRepository.findByQueueToken(activeQueue.token)
        assertThat(savedWallet).isNotNull
        assertThat(savedWallet!!.balance).isEqualTo(5000L)
    }

    @Test
    @DisplayName("지갑을 조회할떄 토큰이 활성화 되어 있지 않다면, 에러를 반환한다")
    fun `when get wallet and queue token is not active, then throw error`() {
        val nonActiveQueue = activeQueue.copy(status = QueueStatus.EXPIRED)
        queueRepository.update(nonActiveQueue)

        val getQuery =
            GetWalletBalanceQuery(
                queueToken = nonActiveQueue.token,
            )

        assertThatThrownBy { sut.get(getQuery) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Queue status is not active")
    }

    @Test
    @DisplayName("지갑을 조회할때 토큰이 존재하지 않는다면, 에러를 반환한다")
    fun `when get wallet and queue token does not exist, then throw error`() {
        val invalidToken = "invalid-token-789"
        val getQuery =
            GetWalletBalanceQuery(
                queueToken = invalidToken,
            )

        assertThatThrownBy { sut.get(getQuery) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Queue not found by $invalidToken")
    }

    @Test
    @DisplayName("지갑을 조회할떄 지갑이 존재하지 않는다면 0원 지갑을 반환한다")
    fun `when get wallet and wallet does not exist, then return 0 balance wallet`() {
        val newQueueToken = testUtils.issueAndActivateQueueToken().token

        val getQuery =
            GetWalletBalanceQuery(
                queueToken = newQueueToken,
            )

        val wallet = sut.get(getQuery)

        assertThat(wallet).isNotNull
        assertThat(wallet.balance).isEqualTo(0L)
        assertThat(wallet.queueToken).isEqualTo(newQueueToken)
    }

    @Test
    @DisplayName("지갑을 조회할떄 지갑이 존재한다면, 지갑을 반환한다")
    fun `when get wallet and wallet exists, then return existing wallet`() {
        val chargeCommand =
            ChargeWalletCommand(
                queueToken = activeQueue.token,
                amount = 4000L,
            )
        val wallet = sut.charge(chargeCommand)

        val getQuery =
            GetWalletBalanceQuery(
                queueToken = activeQueue.token,
            )

        val retrievedWallet = sut.get(getQuery)

        assertThat(retrievedWallet).isNotNull
        assertThat(retrievedWallet.balance).isEqualTo(4000L)
        assertThat(retrievedWallet.queueToken).isEqualTo(activeQueue.token)
    }
}
