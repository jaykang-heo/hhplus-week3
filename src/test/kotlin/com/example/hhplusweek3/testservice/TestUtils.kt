package com.example.hhplusweek3.testservice

import com.example.hhplusweek3.application.QueueFacade
import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.application.WalletFacade
import com.example.hhplusweek3.domain.command.ChargeWalletCommand
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Queue
import com.example.hhplusweek3.domain.model.QueueStatus
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.QueueRepository
import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.QueueEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.ReservationEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.WalletEntityJpaRepository
import com.example.hhplusweek3.repository.model.ConcertSeatEntity
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class TestUtils(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository,
    private val reservationFacade: ReservationFacade,
    private val queueFacade: QueueFacade,
    private val queueEntityJpaRepository: QueueEntityJpaRepository,
    private val reservationEntityJpaRepository: ReservationEntityJpaRepository,
    private val queueRepository: QueueRepository,
    private val walletEntityJpaRepository: WalletEntityJpaRepository,
    private val walletFacade: WalletFacade,
) {
    fun resetDatabase() {
        queueEntityJpaRepository.deleteAll()
        reservationEntityJpaRepository.deleteAll()
    }

    fun issueQueue(): String = queueFacade.issue(IssueQueueTokenCommand()).token

    fun setQueueToPendingStatus(queueToken: String): Queue {
        val queue = queueEntityJpaRepository.findByToken(queueToken)!!
        queue.status = QueueStatus.PENDING
        val savedQueue = queueEntityJpaRepository.save(queue)
        return savedQueue.toModel()
    }

    fun issue(): Queue = queueFacade.issue(IssueQueueTokenCommand())

    fun createReservation(): Reservation {
        resetDatabase()
        resetConcertSeats()
        val token = queueFacade.issue(IssueQueueTokenCommand()).token
        val date =
            LocalDate
                .now()
                .plusDays(2)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
        walletFacade.charge(ChargeWalletCommand(1000L, token))
        val command = CreateReservationCommand(token, 1L, date)
        val reservation = reservationFacade.reserve(command)
        return reservation
    }

    fun resetConcertSeats(
        fromPlusDay: Long = 1,
        toPlusDay: Long = 10,
    ) {
        concertSeatEntityJpaRepository.deleteAll()
        (fromPlusDay..toPlusDay).map { plusDay ->
            val concertSeats =
                (1..50).map {
                    ConcertSeatEntity(
                        0,
                        LocalDate
                            .now()
                            .plusDays(plusDay)
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC),
                        it.toLong(),
                        1000L,
                    )
                }
            concertSeatEntityJpaRepository.saveAll(concertSeats)
        }
    }

    fun createConcertSeat(
        dateUtc: Instant,
        seatNumber: Long,
        amount: Int = 1,
    ) {
        val concertSeat = ConcertSeatEntity(0, dateUtc = dateUtc, seatNumber = seatNumber, amount = amount.toLong())
        concertSeatEntityJpaRepository.save(concertSeat)
    }

    fun createConcertSeats(
        dateUtc: Instant,
        totalSeats: Int,
    ) {
        for (seatNumber in 1..totalSeats) {
            createConcertSeat(dateUtc, seatNumber.toLong())
        }
    }

    fun resetAndReserveAllSeatsInDate(date: Instant) {
        queueEntityJpaRepository.deleteAll()
        reservationEntityJpaRepository.deleteAll()
        val queues = (1..50).map { queueFacade.issue(IssueQueueTokenCommand()).token }
        queues.mapIndexed { index, s ->
            val command = CreateReservationCommand(s, (index + 1).toLong(), date)
            reservationFacade.reserve(command)
        }
    }

    fun resetAndReserveHalfSeatsInDate(date: Instant) {
        queueEntityJpaRepository.deleteAll()
        reservationEntityJpaRepository.deleteAll()
        val queues = (1..25).map { queueFacade.issue(IssueQueueTokenCommand()).token }

        queues.mapIndexed { index, s ->
            val command = CreateReservationCommand(s, (index + 1).toLong(), date)
            reservationFacade.reserve(command)
        }
    }

    fun resetQueues() {
        queueEntityJpaRepository.deleteAll()
    }

    fun resetWallets() {
        walletEntityJpaRepository.deleteAll()
    }

    fun resetReservations() {
        reservationEntityJpaRepository.deleteAll()
    }

    fun activateQueue(token: String) {
        queueRepository.changeStatusToActive(token)
    }

    fun getActiveQueues(): List<Queue> = queueRepository.findAllActive()

    fun getPendingQueues(): List<Queue> = queueRepository.findAllPending()

    fun issueAndActivateQueueToken(): Queue {
        val command = IssueQueueTokenCommand()
        val queue = queueFacade.issue(command)
        queueRepository.changeStatusToActive(queue.token)
        return queue.copy(status = QueueStatus.ACTIVE)
    }
}
