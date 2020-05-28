  package io.stockgeeks.kafka.stock.ticker.producer.avro;

import io.stockgeeks.stock.tick.avro.StockTick;
import lombok.Value;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class RandomStockTickGenerator {

  private List<Instrument> instruments;
  private static final String STOCK_EXCHANGE_NASDAQ = "NASDAQ";
  private static final String STOCK_EXCHANGE_AMSTERDAM = "AMS";

  private static final String CURRENCY_EURO = "EUR";
  private static final String CURRENCY_US_DOLLAR = "USD";

  public RandomStockTickGenerator() {
    instruments = Arrays.asList(new Instrument("AAPL", STOCK_EXCHANGE_NASDAQ, CURRENCY_US_DOLLAR),
      new Instrument("AMZN", STOCK_EXCHANGE_NASDAQ, CURRENCY_US_DOLLAR),
      new Instrument("GOOGL", STOCK_EXCHANGE_NASDAQ, CURRENCY_US_DOLLAR),
      new Instrument("NFLX", STOCK_EXCHANGE_NASDAQ, CURRENCY_US_DOLLAR),
      new Instrument("INGA", STOCK_EXCHANGE_AMSTERDAM, CURRENCY_EURO),
      new Instrument("AD", STOCK_EXCHANGE_AMSTERDAM, CURRENCY_EURO),
      new Instrument("RDSA", STOCK_EXCHANGE_AMSTERDAM, CURRENCY_EURO));
  }

  StockTick generateRandomStockTick() {
    Instrument randomInstrument = pickRandomInstrument();
    BigDecimal randomPrice = generateRandomPrice();
    return new StockTick(randomInstrument.getSymbol(), randomInstrument.getExchange(), randomPrice.toPlainString(),
                         randomInstrument.getCurrency(), String.valueOf(System.currentTimeMillis()));
  }

  private BigDecimal generateRandomPrice() {
    double leftLimit = 1.000D;
    double rightLimit = 3000.000D;

    BigDecimal randomPrice = BigDecimal.valueOf(new RandomDataGenerator().nextUniform(leftLimit, rightLimit));
    randomPrice = randomPrice.setScale(3, RoundingMode.HALF_UP);
    return randomPrice;
  }

  private Instrument pickRandomInstrument() {
    int randomIndex = new Random().nextInt(instruments.size());
    return instruments.get(randomIndex);
  }

  @Value
  private static class Instrument {
    private String symbol;
    private String exchange;
    private String currency;
  }
}
