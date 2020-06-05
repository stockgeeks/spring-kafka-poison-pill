package io.stockgeeks.kafka.stock.quote.producer.avro;

import io.stockgeeks.stock.quote.avro.StockQuote;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RandomStockQuoteProducer {

  private final StockQuoteProducer stockQuoteProducer;
  private final RandomStockQuoteGenerator randomStockQuoteGenerator;

  public RandomStockQuoteProducer(StockQuoteProducer stockQuoteProducer, RandomStockQuoteGenerator randomStockQuoteGenerator) {
    this.stockQuoteProducer = stockQuoteProducer;
    this.randomStockQuoteGenerator = randomStockQuoteGenerator;
  }

  @Scheduled(fixedRateString = "${stockQuote.producer.rateInMs}")
  public void produceRandomStockQuote() {
    StockQuote stockQuote = randomStockQuoteGenerator.generateRandomStockQuote();
    stockQuoteProducer.produce(stockQuote);
  }
}
