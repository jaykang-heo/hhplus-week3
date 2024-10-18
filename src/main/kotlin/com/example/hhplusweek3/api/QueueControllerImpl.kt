package com.example.hhplusweek3.api

import com.example.hhplusweek3.api.contract.QueueController
import com.example.hhplusweek3.api.response.GetQueueInfoResponse
import com.example.hhplusweek3.api.response.IssueTokenResponse
import com.example.hhplusweek3.api.response.QueueResponse
import com.example.hhplusweek3.application.QueueFacade
import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.query.GetQueueQuery
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/queues")
class QueueControllerImpl(
    private val queueFacade: QueueFacade
) : QueueController {

    override fun issueToken(): IssueTokenResponse {
        val command = IssueQueueTokenCommand()
        val queue = queueFacade.issue(command)
        return IssueTokenResponse(queue.token)
    }

    override fun getQueueInfo(authHeader: String): GetQueueInfoResponse {
        val query = GetQueueQuery(authHeader)
        val queue = queueFacade.get(query)
        return GetQueueInfoResponse(QueueResponse(queue))
    }
}
