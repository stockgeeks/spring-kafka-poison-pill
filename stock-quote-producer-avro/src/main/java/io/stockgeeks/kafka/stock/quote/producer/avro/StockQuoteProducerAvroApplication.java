package io.stockgeeks.kafka.stock.quote.producer.avro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockQuoteProducerAvroApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockQuoteProducerAvroApplication.class, args);
	}

}
