package com.example.hhplusweek3.api.response

import com.example.hhplusweek3.domain.model.Queue
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "대기열 응답 객체")
data class QueueResponse(
    @Schema(
        description = "토큰",
        example = "token_abcdef123456",
        required = true
    )
    val token: String,

    @Schema(
        description = "만료 시간 (UTC)",
        example = "2021-12-31T22:31:0Z",
        required = true
    )
    val expirationTimeUtc: Instant,

    @Schema(
        description = "생성 시간 (UTC)",
        example = "2021-12-31T22:00:00Z",
        required = true
    )
    val createdTimeUtc: Instant,

    @Schema(
        description = "업데이트 시간 (UTC)",
        example = "2021-12-31T22:30:00Z",
        required = true
    )
    val updatedTimeUtc: Instant
) {
    constructor(queue: Queue) : this(
        token = queue.token,
        expirationTimeUtc = queue.expirationTimeUtc,
        createdTimeUtc = queue.createdTimeUtc,
        updatedTimeUtc = queue.updatedTimeUtc
    )
}
