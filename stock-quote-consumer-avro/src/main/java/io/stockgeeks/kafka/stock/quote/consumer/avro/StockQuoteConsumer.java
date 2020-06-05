package io.stockgeeks.kafka.stock.quote.consumer.avro;

import io.stockgeeks.stock.quote.avro.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static io.stockgeeks.kafka.stock.quote.consumer.avro.StockQuoteConsumerAvroApplication.DEAD_LETTER_TOPIC_NAME;
import static io.stockgeeks.kafka.stock.quote.consumer.avro.StockQuoteConsumerAvroApplication.TOPIC_NAME;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION_ID;

@Component
@Slf4j
public class StockQuoteConsumer {

  @KafkaListener(topics = TOPIC_NAME)
  public void listen(StockQuote stockQuote, @Header(RECEIVED_PARTITION_ID) Integer partitionId) {
    log.info("Consumed: {}, {} {} from partition: {}", stockQuote.getSymbol(), stockQuote.getCurrency(),
      stockQuote.getTradeValue(), partitionId);
  }

  @KafkaListener(topics = DEAD_LETTER_TOPIC_NAME, containerFactory = "deadLetterTopicKafkaListenerContainerFactory")
  public void listen(Bytes poisonPill) {
    log.info("Poison pill consumed: {}", poisonPill);
  }

}
