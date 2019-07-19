package com.grpc.server.util;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class CustomAvroDeserializer implements Deserializer {

    private KafkaAvroDeserializer kafkaAvroDeserializer;

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        return kafkaAvroDeserializer.deserialize(topic,headers,data);
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        configs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "localhost:8081");
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return kafkaAvroDeserializer.deserialize(topic,data);
    }

    @Override
    public void close() {

    }
}
