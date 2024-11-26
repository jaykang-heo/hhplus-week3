package com.example.hhplusweek3.api.config

import com.example.hhplusweek3.repository.jpa.ConcertSeatEntityJpaRepository
import com.example.hhplusweek3.repository.model.ConcertSeatEntity
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.ZoneOffset

@Configuration
class ConcertSeatConfiguration(
    private val concertSeatEntityJpaRepository: ConcertSeatEntityJpaRepository,
) {
    private val logger = KotlinLogging.logger(this::class.java.name)

    init {
        logger.info { "Saving concert seats started" }
        concertSeatEntityJpaRepository.deleteAll()
        val fromPlusDay = 0
        val toPlusDay = 50L
        val seatPrice = 1000L
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
                        seatPrice,
                    )
                }
            concertSeatEntityJpaRepository.saveAll(concertSeats)
        }
    }
}
