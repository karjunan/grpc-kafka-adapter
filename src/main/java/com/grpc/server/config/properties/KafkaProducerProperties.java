package com.grpc.server.config.properties;

import com.grpc.server.interceptor.AvroDeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "spring.kafka.producer")
@Getter
@Setter
public class KafkaProducerProperties {


        private final Map<String,String> configmap = new HashMap<>();

        public Map<String, String> getProducerConfiguration() {
                return this.configmap;
        }

//        private String boot_strap_servers;
//        private String schema_registry_url;
//        private String transactional_id;
//        private String enable_idempotence;
//        private String transactional_id_prefix;
//        private String transaction_timeout_ms;
//        private String key_serializer;
//        private String value_serializer;

}
