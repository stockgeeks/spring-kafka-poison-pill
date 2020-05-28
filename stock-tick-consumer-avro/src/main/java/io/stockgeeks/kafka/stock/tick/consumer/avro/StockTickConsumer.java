package io.stockgeeks.kafka.stock.tick.consumer.avro;

import io.stockgeeks.stock.tick.avro.StockTick;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static io.stockgeeks.kafka.stock.tick.consumer.avro.StockTickConsumerAvroApplication.TOPIC_NAME;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION_ID;

@Component
@Slf4j
public class StockTickConsumer {

  @KafkaListener(topics = TOPIC_NAME)
  public void listen(StockTick stockTick, @Header(RECEIVED_PARTITION_ID) Integer partitionId) {
    log.info("Consumed: {}, {} {} from partition: {}", stockTick.getSymbol(), stockTick.getCurrency(),
      stockTick.getTradeValue(), partitionId);
  }

}
