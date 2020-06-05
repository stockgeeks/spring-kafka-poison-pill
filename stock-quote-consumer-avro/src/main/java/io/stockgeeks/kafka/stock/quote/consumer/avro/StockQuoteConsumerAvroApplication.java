package io.stockgeeks.kafka.stock.quote.consumer.avro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockQuoteConsumerAvroApplication {

	static final String TOPIC_NAME = "stock-quotes-avro";

	public static void main(String[] args) {
		SpringApplication.run(StockQuoteConsumerAvroApplication.class, args);
	}

}
