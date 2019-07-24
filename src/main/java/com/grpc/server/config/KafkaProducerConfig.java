package com.grpc.server.config;


import com.grpc.server.config.properties.KafkaProducerProperties;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import lombok.extern.log4j.Log4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
    private KafkaProducerProperties generalProperties;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, GenericRecord> producerFactoryTranscational() {
        Map<String,Object> config = kafkaProperties.buildProducerProperties();
        Map<String, Object> configProps = new HashMap<>(config);
        configProps.put(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                generalProperties.getSchema_registry_url());
        configProps.put(
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                generalProperties.getEnable_idempotence());
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,generalProperties.getTransactional_id());

        //TODO set the right timeout value for prod environment
        configProps.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,generalProperties.getTransaction_timeout_ms());
        configProps.put("transaction.id.prefix",generalProperties.getTransactional_id_prefix());

        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(configProps);
        factory.setTransactionIdPrefix(generalProperties.getTransactional_id_prefix());

        log.info("Configured properties for transactional Producer=> "  + configProps);
        return factory;
    }


    @Bean
    public ProducerFactory<String, GenericRecord> producerFactoryNonTranscational() {
        Map<String,Object> config = kafkaProperties.buildProducerProperties();
        Map<String, Object> configProps = new HashMap<>(config);

        configProps.put(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                generalProperties.getSchema_registry_url());

        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(configProps);
        factory.setTransactionIdPrefix(generalProperties.getTransactional_id_prefix());
        log.info("Configured properties for Non transactional Producer => "  + configProps);
        return factory;
    }


    @Bean
    public KafkaTemplate<String, GenericRecord> kafkaTemplateTranscational() {
        return new KafkaTemplate<String,GenericRecord>(producerFactoryTranscational());
    }

    @Bean
    public KafkaTemplate<String, GenericRecord> kafkaTemplateNonTranscational() {
        return new KafkaTemplate<String,GenericRecord>(producerFactoryNonTranscational());
    }
}
