package com.grpc.server.config;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j
public class KafkaProducerConfig {

    @Autowired
    KafkaProducerProperties kafkaProducerProperties;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProducerProperties.getBootstrapServers());
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaProducerProperties.getKey_serializer());
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaProducerProperties.getValue_serializer());
        configProps.put(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaProducerProperties.getSchema_registry_url());
        log.info("Configured properties => "  + configProps);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
