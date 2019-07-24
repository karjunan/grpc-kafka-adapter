package com.grpc.server.server;

import com.grpc.server.config.properties.GeneralProperties;
import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.service.consumer.ConsumerStreamService;
import com.grpc.server.service.producer.ProducerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Log4j
public class GrpcServer implements ApplicationRunner {

    @Autowired
    private GeneralProperties properties;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ConsumerStreamService consumerStreamService;

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
