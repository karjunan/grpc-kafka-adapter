package com.grpc.server.config.properties;

import com.grpc.server.interceptor.AvroDeSerializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.kafka.consumer")
@Getter
@Setter
public class KafkaConsumerProperties {

    private final Map<String,String> configmap = new HashMap<>();

    public Map<String, String> getConsumerConfiguration() {
        return this.configmap;
    }
}
