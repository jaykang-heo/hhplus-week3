package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.response.GetQueueInfoResponse
import com.example.hhplusweek3.api.response.IssueTokenResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/queue")
class QueueController {

    @PostMapping("/issue/token")
    fun issueToken(): IssueTokenResponse {
        return IssueTokenResponse(
            UUID.randomUUID().toString()
        )
    }

    @GetMapping("/info")
    fun getQueueInfo(
        @RequestHeader("Authorization") authHeader: String
    ): GetQueueInfoResponse {
        return GetQueueInfoResponse(
            UUID.randomUUID().toString(),
            1,
            100,
            Instant.now()
        )
    }
}
