package com.grpc.server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.kafka.producer")
@Getter
@Setter
public class KafkaProducerProperties {

        private String schema_registry_url;
        private String transactional_id;
        private String enable_idempotence;
        private String transactional_id_prefix;
        private String transaction_timeout_ms;

}
