package com.example.hhplusweek3.api.exception

import com.example.hhplusweek3.api.response.ErrorResponse
import com.example.hhplusweek3.domain.model.exception.ApplicationException
import com.example.hhplusweek3.domain.model.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApplicationException::class)
    fun handleApiException(
        ex: ApplicationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Handled ApiException: ${ex.errorCode.name} - ${ex.message}")

        val status = mapErrorCodeToHttpStatus(ex.errorCode)

        val errorResponse =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                errorCode = ex.errorCode.name,
                errorMessage = ex.message ?: "",
                path = extractPath(request),
            )
        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception occurred: ${ex.message}", ex)
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                errorCode = errorCode.name,
                errorMessage = ex.message ?: "Unknown error",
                path = extractPath(request),
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun mapErrorCodeToHttpStatus(errorCode: ErrorCode): HttpStatus =
        when (errorCode) {
            ErrorCode.QUEUE_NOT_FOUND,
            ErrorCode.RESERVATION_NOT_FOUND,
            ErrorCode.WALLET_NOT_FOUND,
            ErrorCode.CONCERT_DATE_NOT_FOUND,
            ErrorCode.CONCERT_SEAT_NOT_FOUND,
            -> HttpStatus.NOT_FOUND
            ErrorCode.INVALID_QUEUE_STATUS,
            ErrorCode.QUEUE_LIMIT_EXCEEDED,
            ErrorCode.INVALID_RESERVATION,
            ErrorCode.ALREADY_PAID_RESERVATION,
            ErrorCode.ACQUIRE_LOCK_FAILED,
            -> HttpStatus.BAD_REQUEST
            ErrorCode.INSUFFICIENT_BALANCE -> HttpStatus.FORBIDDEN
            ErrorCode.INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        }

    private fun extractPath(request: WebRequest): String = request.getDescription(false).substringAfter("uri=")
}
