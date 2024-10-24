package com.example.hhplusweek3.api.interceptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var queueTokenInterceptor: QueueTokenInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(queueTokenInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/v1/queues/**",
            ).order(Ordered.HIGHEST_PRECEDENCE)
    }
}
