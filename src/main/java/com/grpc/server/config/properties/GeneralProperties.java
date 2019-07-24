package com.grpc.server.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties
@EnableConfigurationProperties({KafkaConsumerProperties.class,KafkaProducerProperties.class})
@Getter
@Setter
public class GeneralProperties {

    private Integer grpc_port;

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;

//    @Autowired
//    private KafkaConsumerProperties kafkaConsumerProperties;

}
