package io.stockgeeks.kafka.stock.quote.producer.avro;

import io.stockgeeks.stock.quote.avro.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockQuoteProducer {

  public static final String TOPIC_NAME = "stock-quotes-avro";

  private final KafkaTemplate<String, StockQuote> kafkaTemplate;

  public StockQuoteProducer(KafkaTemplate<String, StockQuote> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void produce(StockQuote stockQuote) {
    log.info("Produce stock quote: {}, {} {}", stockQuote.getSymbol(), stockQuote.getCurrency(), stockQuote.getTradeValue());
    kafkaTemplate.send(TOPIC_NAME, stockQuote.getSymbol(), stockQuote);
  }
}