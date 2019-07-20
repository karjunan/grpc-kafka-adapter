package com.grpc.server;


import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.config.KafkaProducerProperties;
import com.grpc.server.server.GrpcKafkaServer;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.grpc.server" })
@EnableConfigurationProperties(KafkaProducerProperties.class)
@Log4j
public class GrpcServer {

    public static void main(String[] args) throws Exception {
        log.info("Starting Spring boot Grpc Server");
        new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .sources(GrpcServer.class)
                .sources(GrpcKafkaServer.class)
                .build()
                .run(args);

    }
}
