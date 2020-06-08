package io.stockgeeks.kafka.stock.quote.consumer.avro.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

  private final KafkaProperties kafkaProperties;

  public KafkaConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  /**
   * Boot will autowire this into the container factory.
   */
  @Bean
  public SeekToCurrentErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
    return new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer);
  }

  /**
   * Configure the {@link DeadLetterPublishingRecoverer} to publish poison pill bytes to a dead letter topic:
   * "stock-quotes-avro.DLT".
   */
  @Bean
  public DeadLetterPublishingRecoverer publisher(KafkaTemplate<?, ?> bytesTemplate) {
    return new DeadLetterPublishingRecoverer(bytesTemplate);
  }

  /**
   * Java configuration to consume the bytes of the poison pill from the topic: stock-quotes-avro.DLT
   * See: {@link io.stockgeeks.kafka.stock.quote.consumer.avro.StockQuoteConsumer#listen(Bytes)}
   */
  @Bean
  KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> deadLetterTopicKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(deadLetterTopicConsumerFactory());
    factory.setConcurrency(3);
    factory.getContainerProperties().setPollTimeout(3000);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, Object> deadLetterTopicConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(deadLetterTopicConsumerConfigs());
  }

  @Bean
  public Map<String, Object> deadLetterTopicConsumerConfigs() {
    Map<String, Object> consumerProperties = new HashMap<>();
    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, BytesDeserializer.class);

    return consumerProperties;
  }

}