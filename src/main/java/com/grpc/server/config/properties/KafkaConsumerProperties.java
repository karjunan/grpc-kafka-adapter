package com.grpc.server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.kafka.consumer.streams")
@Getter
@Setter
public class KafkaConsumerProperties {

    private final Map<String,String> configmap = new HashMap<>();

    public Map<String, String> getConsumerConfiguration() {
        return this.configmap;
    }

//    private String application_id;
//    private String bootstrap_servers;
//    private String default_key_serde;
//    private String default_value_serde;
//    private String group_id;
//    private String schema_registry_url;
//    private String auto_offset_reset;
//    private List<String> topic;
}
