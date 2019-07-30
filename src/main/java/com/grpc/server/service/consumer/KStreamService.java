package com.grpc.server.service.consumer;

import com.grpc.server.util.Utils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
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
    public KStream<?,byte[]> process(KStream<?, GenericRecord> genericRecordKStream) {

        return genericRecordKStream
                .map(((key, value) -> {
                    String str = value.toString();
                    return new KeyValue<>(null,str);
                }))
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("WordsCount"))
                .toStream()
                .mapValues(((key, value) -> {
                  KeyValue keyValue =  new KeyValue<>(null, new WordCount(key.key(),value,new Date(key.window().start()),new Date(key.window().end())));
                  GenericRecord record = Utils.buildRecord();
                  record.put("value",keyValue);
                  byte[] result = Utils.convertToByteArray(record);
                  return  result;
                }));
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
