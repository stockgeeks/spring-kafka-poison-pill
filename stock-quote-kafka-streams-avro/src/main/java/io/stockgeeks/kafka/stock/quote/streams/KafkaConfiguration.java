package io.stockgeeks.kafka.stock.quote.streams;

import io.stockgeeks.stock.quote.avro.StockQuote;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class KafkaConfiguration {

    private static final String INPUT_TOPIC_NAME = "stock-quotes-avro";
    public static final String OUTPUT_TOPIC_NAME = "stock-quotes-ing-avro";

    @Bean
    public KStream<String, StockQuote> kStream(StreamsBuilder kStreamBuilder) {
        KStream<String, StockQuote> input = kStreamBuilder.stream(INPUT_TOPIC_NAME);
        input.print(Printed.toSysOut());

        // Simple filter to stream all ING stock quotes to topic `stock-quotes-ing-avro`
        KStream<String, StockQuote> output = input.filter((key, value) -> value.getSymbol().equals("INGA"));
        output.to(OUTPUT_TOPIC_NAME);
        return output;
    }
}
