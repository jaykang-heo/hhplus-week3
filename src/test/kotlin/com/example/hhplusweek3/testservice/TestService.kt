package com.example.hhplusweek3.testservice

import com.example.hhplusweek3.application.QueueFacade
import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.QueueEntityJpaRepository
import com.example.hhplusweek3.repository.jpa.ReservationEntityJpaRepository
import com.example.hhplusweek3.repository.model.ConcertSeatEntity
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Service
class TestService(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository,
    private val reservationFacade: ReservationFacade,
    private val queueFacade: QueueFacade,
    private val queueEntityJpaRepository: QueueEntityJpaRepository,
    private val reservationEntityJpaRepository: ReservationEntityJpaRepository
) {
    fun resetDatabase() {
        queueEntityJpaRepository.deleteAll()
        reservationEntityJpaRepository.deleteAll()
    }

    fun issueQueue(): String {
        return queueFacade.issue(IssueQueueTokenCommand()).token
    }

    fun createReservation(): Reservation {
        resetDatabase()
        resetConcertSeats()
        val token = queueFacade.issue(IssueQueueTokenCommand()).token
        val date = LocalDate.now().plusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)
        val command = CreateReservationCommand(token, 1L, date)
        return reservationFacade.reserve(command)
    }

    fun resetConcertSeats(fromPlusDay: Long = 1, toPlusDay: Long = 10) {
        concertSeatEntityJpaRepository.deleteAll()
        (fromPlusDay..toPlusDay).map { plusDay ->
            val concertSeats = (1..50).map {
                ConcertSeatEntity(
                    0,
                    UUID.randomUUID().toString(),
                    LocalDate.now().plusDays(plusDay).atStartOfDay().toInstant(ZoneOffset.UTC),
                    it.toLong(),
                    1000L
                )
            }
            concertSeatEntityJpaRepository.saveAll(concertSeats)
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
}
