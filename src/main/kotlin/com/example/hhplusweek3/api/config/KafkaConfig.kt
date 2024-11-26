package com.example.hhplusweek3.api.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries

@Configuration
class KafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps = HashMap<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringSerializer::class.java
        // Enable idempotence for exactly-once semantics
        configProps[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        configProps[ProducerConfig.ACKS_CONFIG] = "all"
        configProps[ProducerConfig.RETRIES_CONFIG] = 3
        // Batch settings for better throughput
        configProps[ProducerConfig.BATCH_SIZE_CONFIG] = 16384
        configProps[ProducerConfig.LINGER_MS_CONFIG] = 1
        // Buffer settings
        configProps[ProducerConfig.BUFFER_MEMORY_CONFIG] = 33554432 // 32MB

        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(producerFactory())

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val configProps = HashMap<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringDeserializer::class.java
        // Auto commit settings
        configProps[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        configProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        // Fetch settings for better performance
        configProps[ConsumerConfig.FETCH_MIN_BYTES_CONFIG] = 1024
        configProps[ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG] = 500
        configProps[ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG] = 1048576 // 1MB
        // Heartbeat and session timeout
        configProps[ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG] = 3000
        configProps[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = 10000
        // Max poll settings
        configProps[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 500
        configProps[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 300000 // 5 minutes

        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(3)
        factory.isBatchListener = false
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        val errorHandler =
            DefaultErrorHandler(
                { exception, data ->
                    logger.error("Error in consumer after retries exhausted: $exception, data: $data")
                },
                ExponentialBackOffWithMaxRetries(3).apply {
                    initialInterval = 1000L
                    maxInterval = 10000L
                    multiplier = 2.0
                },
            )

        errorHandler.addRetryableExceptions(
            org.apache.kafka.common.errors.RetriableException::class.java,
            org.springframework.kafka.support.serializer.DeserializationException::class.java,
        )

        errorHandler.addNotRetryableExceptions(
            org.apache.kafka.common.errors.AuthorizationException::class.java,
        )

        factory.setCommonErrorHandler(errorHandler)
        return factory
    }

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs = HashMap<String, Any>()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        return KafkaAdmin(configs)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaConfig::class.java)
    }
}
