package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Reservation
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "좌석 예약 응답 객체")
data class ReserveResponse(
    @Schema(
        description = "결제 ID",
        example = "1234-1234-1234",
        nullable = true
    )
    val paymentId: String?,

    @Schema(
        description = "예약 ID",
        example = "1234-1234-1234"
    )
    val reservationId: String,

    @Schema(
        description = "예약된 좌석 번호",
        example = "1"
    )
    val reservedSeatNumber: Long,

    @Schema(
        description = "예약된 시간 (UTC)",
        example = "2021-12-31T23:59:59Z"
    )
    val reservedTimeUtc: Instant,

    @Schema(
        description = "큐 토큰",
        example = "token_abcdef123456"
    )
    val queueToken: String,

    @Schema(
        description = "생성 시간 (UTC)",
        example = "2021-12-31T22:00:00Z"
    )
    val createdTimeUtc: Instant,

    @Schema(
        description = "업데이트 시간 (UTC)",
        example = "2021-12-31T22:30:00Z"
    )
    val updatedTimeUtc: Instant,

    @Schema(
        description = "만료 시간 (UTC)",
        example = "2021-12-31T23:59:59Z"
    )
    val expirationTimeUtc: Instant
) {
    constructor(reservation: Reservation) : this(
        paymentId = reservation.paymentId,
        reservationId = reservation.id,
        reservedSeatNumber = reservation.reservedSeat,
        reservedTimeUtc = reservation.dateTimeUtc,
        queueToken = reservation.queueToken,
        createdTimeUtc = reservation.createdTimeUtc,
        updatedTimeUtc = reservation.updatedTimeUtc,
        expirationTimeUtc = reservation.expirationTimeUtc
    )
}
