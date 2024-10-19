package com.example.hhplusweek3.api.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "대기열 정보 응답 객체")
data class GetQueueInfoResponse(
    @Schema(
        description = "대기열 정보",
        implementation = QueueResponse::class
    )
    val queue: QueueResponse
)
