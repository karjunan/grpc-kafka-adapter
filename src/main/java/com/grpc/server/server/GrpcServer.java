package com.grpc.server.server;

import com.grpc.server.config.properties.GeneralProperties;
import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.service.consumer.ConsumerService;
import com.grpc.server.service.producer.ProducerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
public class GrpcServer implements ApplicationRunner {

    private ProducerService producerService;
    private ConsumerService consumerService;

    @Autowired
    public GrpcServer(ConsumerService consumerService,
               ProducerService producerService) {

        this.consumerService = consumerService;
        this.producerService = producerService;
    }

    @Autowired
    private GeneralProperties properties;


    @Autowired
    HeaderServerInterceptor headerServerInterceptor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }

    private Server server;

    public void start() throws Exception {

        server = ServerBuilder.forPort(properties.getGrpc_port())
                .addService(producerService)
//                .addService(ServerInterceptors.intercept(consumerStreamService,headerServerInterceptor))
                .addService(consumerService)
                .build()
                .start();

        log.info("Listening on port " + properties.getGrpc_port());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.error("Shutting down server");
                GrpcServer.this.stop();
            }
        });

        server.awaitTermination();

    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
