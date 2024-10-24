package com.example.hhplusweek3.api.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class ApplicationFilter : Filter {
    private val logger = KotlinLogging.logger(ApplicationFilter::class.java.name)

    @Throws(IOException::class)
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            val startTime = System.currentTimeMillis()
            logRequestDetails(request)

            try {
                chain.doFilter(request, response)
            } finally {
                val duration = System.currentTimeMillis() - startTime
                logResponseDetails(response, duration)
            }
        } else {
            chain.doFilter(request, response)
        }
    }

    private fun logRequestDetails(request: HttpServletRequest) {
        val method = request.method
        val uri = request.requestURI
        val queryString = request.queryString ?: ""
        val clientIP = request.remoteAddr
        val headers = getHeadersInfo(request)

        logger.info("Incoming Request: method=$method, uri=$uri, query=$queryString, clientIP=$clientIP")
        logger.debug("Request Headers: $headers")
    }

    private fun logResponseDetails(
        response: HttpServletResponse,
        duration: Long,
    ) {
        val status = response.status
        logger.info("Outgoing Response: status=$status, latency=${duration}ms")
    }

    private fun getHeadersInfo(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            val headerValue = request.getHeader(headerName)
            headers[headerName] = headerValue
        }
        return headers
    }
}
