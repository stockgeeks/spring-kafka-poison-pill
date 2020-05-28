package io.stockgeeks.kafka.stock.ticker.producer.avro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockTickProducerAvroApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockTickProducerAvroApplication.class, args);
	}

}
