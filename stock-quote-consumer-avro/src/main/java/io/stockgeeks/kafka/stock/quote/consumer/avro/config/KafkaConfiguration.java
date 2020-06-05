package io.stockgeeks.kafka.stock.quote.consumer.avro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.LoggingErrorHandler;

@Configuration
@EnableKafka
public class KafkaConfiguration {

  /**
   * Boot will autowire this into the container factory.
   */
  @Bean
  public LoggingErrorHandler errorHandler() {
    return new LoggingErrorHandler();
  }
}