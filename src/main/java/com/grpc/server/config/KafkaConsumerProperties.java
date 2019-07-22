package com.grpc.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "spring.kafka.consumer")
@Getter
@Setter
public class KafkaConsumerProperties {

    private String application_id;
    private String group_id;
    private String bootstrap_servers;
    private String auto_offset_reset;
    private String default_key_serde;
    private String default_value_serde;
    private String enable_auto_commit;
    private String isolation_level;
    private String schema_registry_url;


}
