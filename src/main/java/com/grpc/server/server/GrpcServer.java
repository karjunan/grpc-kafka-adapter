package com.grpc.server.server;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.service.ProducerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Log4j
public class GrpcServer implements ApplicationRunner {

    @Value("${grpc.port}")
    private String port;

    @Autowired
    private ProducerService producerService;

    @Autowired
    HeaderServerInterceptor headerServerInterceptor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }

    private Server server;

    public void start() throws Exception {
        server = ServerBuilder.forPort(Integer.parseInt(port))
                .addService(ServerInterceptors.intercept(producerService, headerServerInterceptor))
                .build()
                .start();
        log.info("Listening on port " + port);
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
