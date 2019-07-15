package com.grpc.server;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.service.ProducerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.util.logging.Logger;

public class GrpcKafkaServer {
    private static final Logger logger = Logger.getLogger(GrpcKafkaServer.class.getName());

    public static void main(String[] args) {

        try {
            GrpcKafkaServer grpcKafkaServer = new GrpcKafkaServer();
            grpcKafkaServer.start();
        } catch (Exception e) {
            logger.severe("Problem while starting the producer server " + e.getMessage());
        }
    }
    
    private Server server;
    
    private void start() throws Exception {
        final int port = 8000;
        ProducerService service = new ProducerService();

        server = ServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(service,new HeaderServerInterceptor()))
                .build()
                .start();

        logger.info("Listening on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.severe("Shutting down server");
                GrpcKafkaServer.this.stop();
            }
        });
        
        server.awaitTermination();
        
    }
    
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
