package com.grpc.server.service.consumer;

import com.grpc.server.util.Utils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.Arrays;
import java.util.Date;

/**
 *This application consumes data from a Kafka topic (e.g., words),
 * computes word count for each unique word in a 5 seconds time window,
 * and the computed results are sent to a downstream topic (e.g., counts)
 * for further processing.
 *
*/

@EnableBinding(KStreamService.KStreamProcessor.class)
public class KStreamService {

    @StreamListener("word-input")
    @SendTo("word-output")
    @SuppressWarnings("Unchecked")
    public KStream<?,?> process(KStream<?, GenericRecord> genericRecordKStream) {
       return genericRecordKStream
                .mapValues(((key, value) -> {
                    System.out.println("Printing Values => " + value.toString());
                    return value;
                }))
                .flatMapValues(value ->  Arrays.asList(value.toString().toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
//                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("WordsCount"))
               .toStream()
               .map((key,value) -> new KeyValue<>(key,value));

    }

    public interface KStreamProcessor {

        @Input("word-input")
        KStream<?, ?> input();

        @Output("word-output")
        KStream<?, ?> output();

    }

    @AllArgsConstructor
    private static class WordCount {
       @NonNull private String key;
       @NonNull private Long value;
       @NonNull private java.util.Date startDate;
       @NonNull private Date endDate;

    }



}
