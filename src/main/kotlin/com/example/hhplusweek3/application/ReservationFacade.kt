package com.example.hhplusweek3.application

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.port.ReservationRepository
import com.example.hhplusweek3.domain.service.QueueService
import com.example.hhplusweek3.domain.service.ReservationService
import com.example.hhplusweek3.domain.validator.CreateReservationCommandValidator
import org.springframework.stereotype.Service

@Service
class ReservationFacade(
    private val reservationRepository: ReservationRepository,
    private val queueService: QueueService,
    private val reservationService: ReservationService,
    private val createReservationCommandValidator: CreateReservationCommandValidator
) {

    fun reserve(command: CreateReservationCommand): Reservation {
        queueService.preRun(command.token)
        reservationService.preRun()
        createReservationCommandValidator.validate(command)

        val reservation = Reservation(command)
        return reservationRepository.save(reservation)
    }
}
