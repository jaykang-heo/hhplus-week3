package com.example.hhplusweek3.domain.service

import com.example.hhplusweek3.domain.command.CreateReservationCommand
import com.example.hhplusweek3.domain.model.Reservation
import com.example.hhplusweek3.domain.model.exception.AcquireLockFailedException
import com.example.hhplusweek3.domain.port.LockRepository
import org.springframework.stereotype.Component

@Component
class ReservationService(
    private val lockRepository: LockRepository,
) {
    fun reserveWithLockOrThrow(
        command: CreateReservationCommand,
        action: () -> Reservation,
    ): Reservation =
        lockRepository.acquireReservationLock(command.dateUtc, command.seatNumber) {
            action.invoke()
        }
            ?: throw AcquireLockFailedException("Reservation lock failed::${command.dateUtc}, ${command.seatNumber}, ${command.queueToken}")
}
