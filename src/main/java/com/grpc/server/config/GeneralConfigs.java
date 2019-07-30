package com.grpc.server.config;

import com.grpc.server.interceptor.ByteArrayToGenericRecordMessageConverter;
import org.springframework.cloud.stream.annotation.StreamMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
public class GeneralConfigs {

    @Bean
    @StreamMessageConverter
    public MessageConverter customMessageConverter() {
        return new ByteArrayToGenericRecordMessageConverter();
    }
}
