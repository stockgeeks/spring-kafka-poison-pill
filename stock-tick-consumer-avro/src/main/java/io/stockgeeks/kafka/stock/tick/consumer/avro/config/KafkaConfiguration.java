package io.stockgeeks.kafka.stock.tick.consumer.avro.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerStoppingErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer2;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

/**
 * For more information see:
 * https://docs.spring.io/spring-kafka/reference/html/#error-handling-deserializer
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {

  private final KafkaProperties kafkaProperties;

  @Autowired
  private KafkaTemplate kafkaTemplate;

  public KafkaConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  /**
   * Demo 1 Avro: 'Poison Pills'
   *
   * When a deserializer fails to deserialize a message, Spring has no way to handle the problem, because it occurs
   * before the poll() returns. To solve this problem, version 2.2 introduced the ErrorHandlingDeserializer2.
   * This deserializer delegates to a real deserializer (key or value). If the delegate fails to deserialize the record
   * content, the ErrorHandlingDeserializer2 returns a null value and a DeserializationException in a header that
   * contains the cause and the raw bytes. When you use a record-level MessageListener, if the ConsumerRecord
   * contains a DeserializationException header for either the key or value, the container’s ErrorHandler is
   * called with the failed ConsumerRecord. The record is not passed to the listener.
   *
   * Since Spring Kafka 2.5 {@link ErrorHandlingDeserializer2}  has been deprecated in favour of
   * {@link ErrorHandlingDeserializer}
   */
  // TODO Demo: enable to handle poison pills!
//  @Bean
  public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
    Map<String, Object> configProperties = kafkaProperties.buildConsumerProperties();

    // This is the important part! To configure the ErrorHandlingDeserializer2
    // and key and value deserializer delegate classes
    // You can also configure this in your configuration yml or properties file
    configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    configProperties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    configProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);

    ConsumerFactory<Object, Object> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(configProperties);
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

    // TODO: Still have to look into this part to be able to publish to a dead letter topic in combination with
    // Deserialization exceptions!
    factory.setErrorHandler(new SeekToCurrentErrorHandler(
      new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(5, 5)));

    configurer.configure(factory, kafkaConsumerFactory);
    return factory;
  }
}