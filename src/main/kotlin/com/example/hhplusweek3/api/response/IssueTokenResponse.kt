package com.example.hhplusweek3.api.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "토큰 발급 응답 객체")
data class IssueTokenResponse(
    @Schema(
        description = "발급된 토큰",
        example = "abcdef123456",
        required = true
    )
    val token: String
)
