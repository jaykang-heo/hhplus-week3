package com.example.hhplusweek3.api.interceptor

import com.example.hhplusweek3.domain.validator.QueueValidator
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class QueueTokenInterceptor(
    private val queueValidator: QueueValidator,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        try {
            val authHeader = request.getHeader("Authorization")
            when {
                authHeader.isNullOrBlank() -> {
                    response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing session token",
                    )
                    return false
                }
                !queueValidator.isValid(authHeader) -> {
                    response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid session token",
                    )
                    return false
                }
                else -> return true
            }
        } catch (e: Exception) {
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error processing request",
            )
            return false
        }
    }
}
