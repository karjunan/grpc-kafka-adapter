package com.grpc.server.util;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CustomAvroSerializer implements Serializer {

    private KafkaAvroSerializer kafkaAvroSerializer;

    private final MockSchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();

    public CustomAvroSerializer() {
        kafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient);
    }


    @Override
    public void configure(Map configs, boolean isKey) {
        configs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "localhost:8081");
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        return kafkaAvroSerializer.serialize(topic,data);
    }

    @Override
    public void close() {
        kafkaAvroSerializer.close();
    }
}
