package com.example.hhplusweek3.api.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
@EnableCaching
class RedisCacheConfig {
    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val cacheConfiguration =
            RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues()

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .build()
    }
}
