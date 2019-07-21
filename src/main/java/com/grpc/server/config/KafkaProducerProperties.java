package com.grpc.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "spring.kafka.producer")
@Getter
@Setter
public class KafkaProducerProperties {

        private String bootstrap_servers;
        private String key_serializer;
        private String value_serializer;
        private String schema_registry_url;
        private String transactional_id;
        private String enable_idempotence;
        private String transactional_id_prefix;
        private String transaction_timeout_ms;

}
