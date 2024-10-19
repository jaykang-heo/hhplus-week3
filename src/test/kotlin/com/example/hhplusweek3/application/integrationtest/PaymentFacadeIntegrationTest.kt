package com.example.hhplusweek3.application.integrationtest

import com.example.hhplusweek3.application.PaymentFacade
import com.example.hhplusweek3.domain.command.CreatePaymentCommand
import com.example.hhplusweek3.testservice.TestService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class PaymentFacadeIntegrationTest(
    @Autowired val sut: PaymentFacade,
    @Autowired val testService: TestService
) {

    @Test
    @DisplayName("결제를 할때 예약 번호가 존재하지 않는다면, 에러를 반환한다")
    fun `when create payment and reservation id does not exist, then throw error`() {
        // given
        val queueToken = testService.issueQueue()
        val command = CreatePaymentCommand(queueToken, UUID.randomUUID().toString())

        // when, then
        val actual = assertThrows<RuntimeException> { sut.createPayment(command) }
        assertThat(actual.message).contains("reservation token not found by")
    }

    @Test
    @DisplayName("결제를 할때 결제 요청이 정상이라면, 성공한다")
    fun `when create payment and request is valid, then succeed`() {
        // given
        val reservation = testService.createReservation()
        val command = CreatePaymentCommand(reservation.queueToken, reservation.id)

        // when
        val actual = sut.createPayment(command)

        // then
        assertThat(actual.reservationId).isEqualTo(reservation.id)
        assertThat(actual.amount).isEqualTo(1000L)
    }
}
