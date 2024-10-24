package com.example.hhplusweek3.domain.validator

import com.example.hhplusweek3.domain.command.IssueQueueTokenCommand
import com.example.hhplusweek3.domain.model.exception.QueueLimitExceededException
import com.example.hhplusweek3.domain.port.QueueLimitRepository
import org.springframework.stereotype.Component

@Component
class IssueQueueTokenCommandValidator(
    private val queueLimitRepository: QueueLimitRepository,
) {
    fun validate(command: IssueQueueTokenCommand) {
        val queueLimit = queueLimitRepository.getQueueLimit()
        if (queueLimit.issueCount >= queueLimit.issueLimitCount) {
            throw QueueLimitExceededException(queueLimit.issueCount, queueLimit.issueLimitCount)
        }
    }
}
