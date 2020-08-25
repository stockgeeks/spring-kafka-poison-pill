package io.stockgeeks.kafka.stock.quote.streams;

import io.stockgeeks.stock.quote.avro.StockQuote;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

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

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(KafkaProperties kafkaProperties, DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        Map<String, Object> properties = kafkaProperties.getStreams().buildProperties();

        properties.put(RecoveringDeserializationExceptionHandler.KSTREAM_DESERIALIZATION_RECOVERER, deadLetterPublishingRecoverer);

        return new KafkaStreamsConfiguration(properties);
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<?, ?> bytesTemplate) {
        Map<Class<?>, KafkaOperations<? extends Object, ? extends Object>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, bytesTemplate);

        return new DeadLetterPublishingRecoverer(templates);
    }
}
