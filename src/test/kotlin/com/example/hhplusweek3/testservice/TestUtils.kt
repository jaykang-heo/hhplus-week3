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
import com.example.hhplusweek3.domain.service.ConcertService
import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.PaymentEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.QueueEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.WalletEntityJpaRepository
import com.example.hhplusweek3.repository.model.ConcertSeatEntity
import com.example.hhplusweek3.repository.redis.ReservationEntityRepository
import mu.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class TestUtils(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository,
    private val reservationFacade: ReservationFacade,
    private val queueFacade: QueueFacade,
    private val queueEntityJpaRepository: QueueEntityJpaRepository,
    private val reservationEntityRepository: ReservationEntityRepository,
    private val queueRepository: QueueRepository,
    private val walletEntityJpaRepository: WalletEntityJpaRepository,
    private val walletFacade: WalletFacade,
    private val paymentEntityJpaRepository: PaymentEntityJpaRepository,
    private val concertService: ConcertService,
) {
    fun resetDatabase() {
        queueEntityJpaRepository.deleteAll()
        reservationEntityRepository.deleteAll()
        paymentEntityJpaRepository.deleteAll()
        resetConcertSeats()
    }

    fun issueQueueToken(): String = queueFacade.issue(IssueQueueTokenCommand()).token

    fun setQueueToPendingStatus(queueToken: String): Queue {
        val queue = queueEntityJpaRepository.findByToken(queueToken)!!
        queue.status = QueueStatus.PENDING
        val savedQueue = queueEntityJpaRepository.save(queue)
        return savedQueue.toModel()
    }

    fun issue(): Queue = queueFacade.issue(IssueQueueTokenCommand())

    fun createReservation(
        queueToken: String? = null,
        plusDays: Long = 2,
    ): Reservation {
        val chargeAmount = 10000L
        val token = queueToken ?: queueFacade.issue(IssueQueueTokenCommand()).token
        val date =
            LocalDate
                .now()
                .plusDays(plusDays)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
        walletFacade.charge(ChargeWalletCommand(chargeAmount, token))
        val command = CreateReservationCommand(token, plusDays, date)
        val reservation = reservationFacade.reserve(command)
        return reservation
    }

    fun createReservations(
        count: Int,
        queueToken: String? = null,
        amount: Long? = null,
    ): List<Reservation> {
        val token = queueToken ?: queueFacade.issue(IssueQueueTokenCommand()).token
        val chargeAmount = amount ?: (SEAT_PRICE * count)
        walletFacade.charge(ChargeWalletCommand(chargeAmount, token))
        return (1..count).map {
            val availableConcert = concertService.getAvailableSchedules().random()
            val command = CreateReservationCommand(token, availableConcert.seats.random().number, availableConcert.date)
            reservationFacade.reserve(command)
        }
    }

    @CacheEvict(
        cacheNames = [
            "concertSeats",
        ],
        allEntries = true,
    )
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
                        SEAT_PRICE,
                    )
                }
            concertSeatEntityJpaRepository.saveAll(concertSeats)
        }
    }

    fun resetAndReserveAllSeatsInDate(date: Instant) {
        queueEntityJpaRepository.deleteAll()
        reservationEntityRepository.deleteAll()
        val queues = (1..50).map { queueFacade.issue(IssueQueueTokenCommand()).token }
        queues.mapIndexed { index, s ->
            val command = CreateReservationCommand(s, (index + 1).toLong(), date)
            reservationFacade.reserve(command)
        }
    }

    fun resetAndReserveHalfSeatsInDate(date: Instant) {
        queueEntityJpaRepository.deleteAll()
        reservationEntityRepository.deleteAll()
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
        reservationEntityRepository.deleteAll()
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

    fun <T> asyncRun(
        threadCount: Int,
        commands: List<T>,
        action: (T) -> Unit,
    ) {
        val startLatch = CountDownLatch(1)
        val endLatch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)

        try {
            val futures =
                commands.map { command ->
                    CompletableFuture.supplyAsync({
                        try {
                            startLatch.await()
                            action(command)
                            null
                        } catch (e: Exception) {
//                            logger.error { e }
                        } finally {
                            endLatch.countDown()
                        }
                    }, executor)
                }

            startLatch.countDown()
            endLatch.await()
            futures.forEach { it.join() }
        } finally {
            executor.shutdown()
            executor.awaitTermination(10, TimeUnit.SECONDS)
        }
    }

    companion object {
        val logger = KotlinLogging.logger(TestUtils::class.java.name)
        const val SEAT_PRICE = 1000L
    }
}
