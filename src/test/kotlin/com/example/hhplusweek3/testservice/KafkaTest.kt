package com.example.hhplusweek3.testservice

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration
import java.util.concurrent.TimeUnit

@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = ["test-topic"],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
)
@DirtiesContext
class KafkaTest {
    companion object {
        const val TOPIC = "test-topic"
        const val MESSAGE = "test message"
    }

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Test
    fun testKafkaConnection() {
        // given
        val consumerProps =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to
                    kafkaTemplate.producerFactory.configurationProperties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG],
                ConsumerConfig.GROUP_ID_CONFIG to "test-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            )

        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(consumerProps)
        val consumer = consumerFactory.createConsumer()

        consumer.subscribe(listOf(TOPIC))

        // when
        val sendResult =
            kafkaTemplate
                .send(TOPIC, MESSAGE)
                .get(5, TimeUnit.SECONDS)

        // then
        assertThat(sendResult.recordMetadata.topic()).isEqualTo(TOPIC)
        assertThat(sendResult.producerRecord.value()).isEqualTo(MESSAGE)
        val records = consumer.poll(Duration.ofSeconds(10))
        assertThat(records.count()).isEqualTo(1)
        val record = records.records(TOPIC).iterator().next()
        assertThat(record.value()).isEqualTo(MESSAGE)
        consumer.close()
    }
}
