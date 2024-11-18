package com.example.hhplusweek3.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableRetry
@EnableScheduling
class RetryConfig {
    @Bean
    fun retryTemplate(): RetryTemplate =
        RetryTemplate().apply {
            setRetryPolicy(SimpleRetryPolicy(3))
            setBackOffPolicy(
                ExponentialBackOffPolicy().apply {
                    initialInterval = 1000L
                    multiplier = 2.0
                    maxInterval = 10000L
                },
            )
        }
}
