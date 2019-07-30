package com.grpc.server;


import com.grpc.server.config.properties.GeneralProperties;
import com.grpc.server.config.properties.KafkaProducerProperties;
import com.grpc.server.server.GrpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.grpc.server" })
@EnableConfigurationProperties({GeneralProperties.class,KafkaProducerProperties.class
       ,GeneralProperties.class})
@Slf4j
public class GrpcApplication {

    public static void main(String[] args) throws Exception {
        log.info("Starting Spring boot Grpc Server");
        new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .sources(GrpcApplication.class)
                .sources(GrpcServer.class)
                .build()
                .run(args);

    }
}
