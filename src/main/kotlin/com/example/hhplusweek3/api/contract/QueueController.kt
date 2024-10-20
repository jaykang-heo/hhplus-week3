package com.example.hhplusweek3.api.contract

import com.example.hhplusweek3.api.response.ErrorResponse
import com.example.hhplusweek3.api.response.GetQueueInfoResponse
import com.example.hhplusweek3.api.response.IssueTokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

interface QueueController {

    @Operation(
        summary = "토큰 발급",
        description = "인증을 위한 새 토큰을 발급합니다.",
        tags = ["대기열 관리"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "토큰 발급 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = IssueTokenResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "토큰 발급 오류",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping("/tokens")
    fun issueToken(): IssueTokenResponse

    @Operation(
        summary = "대기열 정보 조회",
        description = "현재 대기열 상태에 대한 정보를 조회합니다.",
        tags = ["대기열 관리"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "대기열 정보 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GetQueueInfoResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않음",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "대기열 정보 조회 오류",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @GetMapping
    fun getQueueInfo(
        @Parameter(
            description = "인증 헤더 (예: {token})",
            required = true,
            example = "abcdef123456"
        )
        @RequestHeader("Authorization")
        authHeader: String
    ): GetQueueInfoResponse
}
