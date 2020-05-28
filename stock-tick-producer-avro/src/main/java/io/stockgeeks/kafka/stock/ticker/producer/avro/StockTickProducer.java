package io.stockgeeks.kafka.stock.ticker.producer.avro;

import io.stockgeeks.stock.tick.avro.StockTick;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockTickProducer {

  public static final String TOPIC_NAME = "stock-ticks-avro";

  private final KafkaTemplate<String, StockTick> kafkaTemplate;

  public StockTickProducer(KafkaTemplate<String, StockTick> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void produce(StockTick stockTick) {
    log.info("Produce stock tick: {}, {} {}", stockTick.getSymbol(), stockTick.getCurrency(), stockTick.getTradeValue());
    kafkaTemplate.send(TOPIC_NAME, stockTick.getSymbol(), stockTick);
  }
}