package com.example.hhplusweek3.config

import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
// import org.testcontainers.containers.GenericContainer
// import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestRedisConfiguration {
    companion object {
//        private val redis =
//            GenericContainer(DockerImageName.parse("redis:7.2"))
//                .withExposedPorts(6379)
//                .apply { start() }

//        @JvmStatic
//        @DynamicPropertySource
//        fun properties(registry: DynamicPropertyRegistry) {
//            registry.add("spring.data.redis.host") { redis.host }
//            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
//        }
    }

    private var redissonClient: RedissonClient? = null

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val configuration =
            RedisStandaloneConfiguration().apply {
                hostName = "localhost"
                port = 6379
            }
        return LettuceConnectionFactory(configuration)
    }

    @Bean
    @Primary
    fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply {
            setConnectionFactory(redisConnectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()
            afterPropertiesSet()
        }

    @Bean
    fun redissonClient(): RedissonClient {
        val config =
            Config().apply {
                useSingleServer()
                    .setAddress("redis://localhost:6379")
                    .setTimeout(5000)
                    .setConnectionMinimumIdleSize(1)
            }

        return Redisson.create(config).also {
            redissonClient = it
        }
    }

    @PreDestroy
    fun cleanup() {
        redissonClient?.shutdown()
    }
}
