package com.example.hhplusweek3.application.integrationtest.concurrency

import com.example.hhplusweek3.application.PaymentFacade
import com.example.hhplusweek3.testservice.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaymentFacadeCreatePaymentConcurrencyIntegrationTest(
    @Autowired private val paymentFacade: PaymentFacade,
    @Autowired private val testUtils: TestUtils,
) {
    @Test
    fun `동시에 여러 결제를 할떄, 한번만 성공한다`() {
        // given

        // when

        // then
    }

    @Test
    fun `동시에 여러 결제를 할때, 지갑 잔액은 한번만 차감된다`() {
        // given

        // when

        // then
    }

    @Test
    fun `동시에 다른 결제를 할때, 모두 각각 정상적으로 결제된다`() {
        // given

        // when

        // then
    }

    @Test
    fun `동시에 예약 만료와 결제 시도를 할때 둘 중 하나의 트랜잭션은 성공한다`() {
        // given

        // when

        // then
    }

    @Test
    fun `동시에 결제와 포인트 충전을 할때 둘 중 하나의 트랜잭션은 성공한다`() {
        // given

        // when

        // then
    }
}
