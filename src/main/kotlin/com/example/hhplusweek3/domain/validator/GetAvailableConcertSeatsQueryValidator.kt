package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.port.ConcertSeatRepository
import com.example.hhplusweek3.domain.query.FindAvailableConcertSeatsQuery
import org.springframework.stereotype.Component

@Component
class GetAvailableConcertSeatsQueryValidator(
    private val concertSeatRepository: ConcertSeatRepository
) {

    fun validate(query: FindAvailableConcertSeatsQuery) {
        query.validate()

        val isExists = concertSeatRepository.existsByDate(query.dateUtc)
        if (!isExists) {
            throw RuntimeException("${query.dateUtc} does not exist")
        }
    }
}
