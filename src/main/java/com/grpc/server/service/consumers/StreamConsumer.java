package com.grpc.server.service.consumers;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;

//@EnableBinding(KafkaStreamsProcessor.class)
public class StreamConsumer {

//    public static final String INPUT_TOPIC = "t1";
//    public static final int WINDOW_SIZE_MS = 30000;
//
//    @StreamListener(INPUT_TOPIC)
//    public KStream<String,GenericRecord> process(KStream<String,GenericRecord>  input) {
//        return input.map((k,v) -> new KeyValue<>(k,v));
//
//    }

}
