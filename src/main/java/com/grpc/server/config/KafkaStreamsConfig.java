package com.grpc.server.config;

import com.grpc.server.config.properties.GeneralProperties;
import com.grpc.server.config.properties.KafkaConsumerProperties;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Properties;

@Configuration
@EnableKafka
@EnableKafkaStreams
@Log4j
@ConditionalOnProperty(name = "consumerBinding", havingValue = "true")
public class KafkaStreamsConfig {

    @Autowired
    private GeneralProperties generalProperties;


    @Autowired
    KafkaConsumerProperties kafkaConsumerProperties;


    public Properties config() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConsumerProperties.getApplication_id());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroup_id());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrap_servers());
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConsumerProperties.getSchema_registry_url());
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed");

        log.info("Configured properties for Stream => "  + props);
        return props;
    }

}
