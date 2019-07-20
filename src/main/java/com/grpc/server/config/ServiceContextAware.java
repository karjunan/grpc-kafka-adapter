package com.grpc.server.config;

import com.grpc.server.server.GrpcKafkaServer;
import com.grpc.server.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;


public class ServiceContextAware  {

//    @Autowired
//    private static GenericApplicationContext applicationContext;
//
//
//    public static ApplicationContext getContext() {
//        return applicationContext;
//    }
//
//    @Bean
//    public ProducerService getProducerService() {
//
//        return new ProducerService();
//    }
//
//    @Bean
//    public KafkaProducerConfig getKafkaProducerConfig() {
//
//        return new KafkaProducerConfig();
//    }
//
//
//    @Bean
//    public GrpcKafkaServer getGrpcKafkaServer() {
//
//        return new GrpcKafkaServer();
//    }
//
//    @Bean
//    public KafkaProducerProperties getKafkaProducerProperties() {
//
//        return new KafkaProducerProperties();
//    }
}
