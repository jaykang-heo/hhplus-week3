package com.example.hhplusweek3.application.integrationtest

import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.testservice.TestService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@Transactional
class ReservationFacadeIntegrationTest(
    @Autowired private val sut: ReservationFacade,
    @Autowired private val testService: TestService,
    @Autowired private val reservationRepository: ReservationRepository,
    @Autowired private val queueRepository: QueueRepository
) {

    private lateinit var activeQueue: Queue
    private val testDate: Instant = Instant.parse("2024-10-20T00:00:00Z")
    private val testSeatNumber: Long = 1L

    @BeforeEach
    fun setup() {
        testService.resetQueues()
        testService.resetDatabase()
        testService.resetReservations()
        testService.resetConcertSeats()
        testService.createConcertSeats(testDate, totalSeats = 100)
        activeQueue = testService.issueAndActivateQueueToken()
    }

    @Test
    @DisplayName("예약할때 만료된 예약이 있다면 만료 시킨다")
    fun `when make reservation and there are expired reservations, then expire expired reservations`() {
        val expiredReservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )
        val expiredReservation = Reservation(
            id = "expired-reservation-id",
            paymentId = null,
            queueToken = activeQueue.token,
            dateTimeUtc = testDate,
            reservedSeat = testSeatNumber,
            amount = 1,
            createdTimeUtc = Instant.now().minusSeconds(600),
            updatedTimeUtc = Instant.now().minusSeconds(600),
            expirationTimeUtc = Instant.now().minusSeconds(300)
        )
        reservationRepository.save(expiredReservation)

        val existingReservation = reservationRepository.findReservationBySeatNumberAndDate(testDate, testSeatNumber)
        assertThat(existingReservation).isNotNull
        assertThat(existingReservation!!.expirationTimeUtc).isBefore(Instant.now())
        assertThat(existingReservation.paymentId).isNull()

        val newReservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )
        val newReservation = sut.reserve(newReservationCommand)

        val deletedReservation = reservationRepository.findReservationBySeatNumberAndDate(testDate, testSeatNumber)
        assertThat(deletedReservation).isNotNull
        assertThat(deletedReservation?.paymentId).isNull()
    }

    @Test
    @DisplayName("예약할떄 사용하는 대기열 토큰이 만료됐다면, 에러를 반환한다")
    fun `when make reservation and the queue token is expire, then throw error`() {
        activeQueue = activeQueue.copy(status = QueueStatus.EXPIRED)
        queueRepository.update(activeQueue)

        val reservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )

        assertThatThrownBy { sut.reserve(reservationCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("queue status must be active EXPIRED")
    }

    @Test
    @DisplayName("예약할떄 사용하는 대기열 토큰이 활성화 되어 있지 않다면, 에러를 반환한다")
    fun `when make reservation and the queue token is not active, then throw error`() {
        testService.resetQueues()
        val pendingQueue = testService.issue()
        assertThat(pendingQueue.status).isEqualTo(QueueStatus.ACTIVE)

        val reservationCommand = CreateReservationCommand(
            token = pendingQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )

        assertThatThrownBy { sut.reserve(reservationCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("queue status must be active PENDING")
    }

    @Test
    @DisplayName("예약할때 예약하려는 좌석이 존재하지 않는다면, 에러를 반환한다")
    fun `when make reservation and desired seat does not exist, then throw error`() {
        val nonExistentSeatNumber = 999L
        val reservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = nonExistentSeatNumber
        )

        assertThatThrownBy { sut.reserve(reservationCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("concert by date $testDate and seat number $nonExistentSeatNumber not found")
    }

    @Test
    @DisplayName("예약할떄 예약하려는 좌석이 이미 선점되어 있다면, 에러를 반환한다")
    fun `when make reservation and the seat is already reserved, then throw error`() {
        val initialReservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )
        sut.reserve(initialReservationCommand)

        val duplicateReservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )

        assertThatThrownBy { sut.reserve(duplicateReservationCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Seat $testSeatNumber by date $testDate already reserved")
    }

    @Test
    @DisplayName("예약할떄 유저가 이미 예약한 좌석이 있다면 에러를 반환한다")
    fun `when make reservation and user already has reserved seat, then throw error`() {
        val initialReservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )
        sut.reserve(initialReservationCommand)

        val secondReservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber + 1
        )

        assertThatThrownBy { sut.reserve(secondReservationCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Token ${activeQueue.token} already reserved seat $testSeatNumber, date $testDate")
    }

    @Test
    @DisplayName("예약할떄 예약 요청이 정상적이라면, 성공한다")
    fun `when make reservation and request is valid, then succeed`() {
        val reservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )

        val reservation = sut.reserve(reservationCommand)

        assertThat(reservation).isNotNull
        assertThat(reservation.queueToken).isEqualTo(activeQueue.token)
        assertThat(reservation.dateTimeUtc).isEqualTo(testDate)
        assertThat(reservation.reservedSeat).isEqualTo(testSeatNumber)
        assertThat(reservation.paymentId).isNull()
        assertThat(reservation.expirationTimeUtc).isEqualTo(reservation.createdTimeUtc.plusSeconds(300))
    }

    @Test
    @DisplayName("예약할때 예약된 좌석의 만료 시간은 5분이다")
    fun `when make reservation, the reserved seat expiration time is 5 min`() {
        val reservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )
        val expectedExpiration = Instant.now().plusSeconds(300) // 5 minutes

        val reservation = sut.reserve(reservationCommand)

        assertThat(reservation.expirationTimeUtc)
            .isCloseTo(expectedExpiration, within(2, ChronoUnit.SECONDS))
    }

    @Test
    @DisplayName("예약할때 초기 예약 내역의 결제 정보는 빈값이다")
    fun `when make reservation and initial reservation paymentId is null`() {
        val reservationCommand = CreateReservationCommand(
            token = activeQueue.token,
            dateUtc = testDate,
            seatNumber = testSeatNumber
        )

        val reservation = sut.reserve(reservationCommand)

        assertThat(reservation.paymentId).isNull()
    }
}
