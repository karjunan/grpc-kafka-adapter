package com.grpc.server.config;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import lombok.extern.log4j.Log4j;
import org.apache.avro.generic.GenericRecord;
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
    private KafkaProducerProperties kafkaProducerProperties;

    @Bean
    public ProducerFactory<String, GenericRecord> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProducerProperties.getBootstrap_servers());
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaProducerProperties.getKey_serializer());
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaProducerProperties.getValue_serializer());
        configProps.put(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaProducerProperties.getSchema_registry_url());
        configProps.put(
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                kafkaProducerProperties.getEnable_idempotence());
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,kafkaProducerProperties.getTransactional_id());

        //TODO set the right timeout value for prod environment
        configProps.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,kafkaProducerProperties.getTransaction_timeout_ms());
        configProps.put("transaction-id-prefix",kafkaProducerProperties.getTransactional_id_prefix());


        log.info("Configured properties => "  + configProps);
        ProducerFactory<String,GenericRecord> factory = new DefaultKafkaProducerFactory<>(configProps);
      ((DefaultKafkaProducerFactory<String, GenericRecord>) factory).setTransactionIdPrefix(kafkaProducerProperties.getTransactional_id_prefix());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, GenericRecord> kafkaTemplate() {
        return new KafkaTemplate<String,GenericRecord>(producerFactory());
    }
}
