package com.grpc.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "producer")
@Getter
@Setter
public class KafkaProducerProperties {

        private String bootstrapServers;
        private String key_serializer;
        private String value_serializer;
        private String schema_registry_url;

}
