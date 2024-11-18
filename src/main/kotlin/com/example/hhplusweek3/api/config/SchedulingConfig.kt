package com.example.hhplusweek3.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableScheduling
class SchedulingConfig {
    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 4
        executor.queueCapacity = 10
        executor.setThreadNamePrefix("outbox-relay-")
        return executor
    }
}
