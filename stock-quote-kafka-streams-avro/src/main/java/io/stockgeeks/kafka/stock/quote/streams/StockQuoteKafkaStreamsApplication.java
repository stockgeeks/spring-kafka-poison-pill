package io.stockgeeks.kafka.stock.quote.streams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockQuoteKafkaStreamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockQuoteKafkaStreamsApplication.class, args);
	}

}
