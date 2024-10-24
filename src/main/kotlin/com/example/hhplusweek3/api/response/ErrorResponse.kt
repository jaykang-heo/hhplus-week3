package com.example.hhplusweek3.api.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "에러 응답 객체")
data class ErrorResponse(
    @Schema(
        description = "에러 코드",
        example = "INVALID_INPUT",
    )
    val errorCode: String,
    @Schema(
        description = "에러 메시지",
        example = "입력 값이 유효하지 않습니다.",
    )
    val errorMessage: String,
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val path: String,
)
