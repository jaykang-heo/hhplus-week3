package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.contract.ReservationController
import com.example.hhplusweek3.api.request.ReserveRequest
import com.example.hhplusweek3.api.response.ReserveResponse
import com.example.hhplusweek3.application.ReservationFacade
import com.example.hhplusweek3.domain.command.CreateReservationCommand
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reservations")
class ReservationControllerImpl(
    private val reservationFacade: ReservationFacade
) : ReservationController {

    override fun reserve(request: ReserveRequest, authorization: String): ReserveResponse {
        val command = CreateReservationCommand(authorization, request.seatNumber, request.dateUct)
        val reservation = reservationFacade.reserve(command)
        return ReserveResponse(reservation)
    }
}
