package com.example.hhplusweek3.application.integrationtest.concurrency

import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.testservice.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ReservationFacadeReserveConcurrencyIntegrationTest(
    @Autowired private val reservationFacade: ReservationFacade,
    @Autowired private val testUtils: TestUtils,
) {
    @Test
    fun `동시에 같은 예약을 여러번 할때, 예약은 한번만 된다`() {
        // given

        // when

        // then
    }
}
