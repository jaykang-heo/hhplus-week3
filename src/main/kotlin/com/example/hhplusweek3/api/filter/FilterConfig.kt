package com.example.hhplusweek3.api.filter

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class FilterConfig {
    @Bean
    fun queueTokenFilter(): FilterRegistrationBean<ApplicationFilter> {
        val registrationBean = FilterRegistrationBean<ApplicationFilter>()
        registrationBean.filter = ApplicationFilter()
        registrationBean.addUrlPatterns("/api/*")
        registrationBean.order = Ordered.HIGHEST_PRECEDENCE
        return registrationBean
    }
}
