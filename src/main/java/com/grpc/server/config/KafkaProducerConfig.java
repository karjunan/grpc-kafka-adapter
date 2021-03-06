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
        Map<String, Object> configProps = new HashMap<>(kafkaProducerProperties.getProducerConfiguration());
        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(configProps);
        factory.setTransactionIdPrefix(kafkaProducerProperties.getProducerConfiguration().get("transactional.id.prefix"));
        log.info("Configured properties for transactional Producer=> "  + configProps);
        return factory;
    }


    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplateTranscational() {
        return new KafkaTemplate(producerFactoryTranscational());
    }

}
