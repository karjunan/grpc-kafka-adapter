package com.grpc.server.config;


import com.grpc.server.config.properties.KafkaProducerProperties;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "producerBinding", havingValue = "true")
public class KafkaProducerConfig {

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;


    @Bean
    public ProducerFactory<String, byte[]> producerFactoryTranscational() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProducerProperties.getBoot_strap_servers());
        configProps.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaProducerProperties.getSchema_registry_url());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaProducerProperties.getKey_serializer());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,kafkaProducerProperties.getValue_serializer());
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProducerProperties.getEnable_idempotence());
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,kafkaProducerProperties.getTransactional_id());

        //TODO set the right timeout value for prod environment
        configProps.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,kafkaProducerProperties.getTransaction_timeout_ms());
        configProps.put("transaction.id.prefix",kafkaProducerProperties.getTransactional_id_prefix());

        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(configProps);
        factory.setTransactionIdPrefix(kafkaProducerProperties.getTransactional_id_prefix());

        log.info("Configured properties for transactional Producer=> "  + configProps);
        return factory;
    }


    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplateTranscational() {
        return new KafkaTemplate(producerFactoryTranscational());
    }

}
