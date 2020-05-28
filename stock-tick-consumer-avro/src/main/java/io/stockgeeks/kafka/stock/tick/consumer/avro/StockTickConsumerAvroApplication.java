package io.stockgeeks.kafka.stock.tick.consumer.avro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTickConsumerAvroApplication {

	static final String TOPIC_NAME = "stock-ticks-avro";

	public static void main(String[] args) {
		SpringApplication.run(StockTickConsumerAvroApplication.class, args);
	}

}
