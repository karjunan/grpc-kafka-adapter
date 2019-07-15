package com.grpc.server;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.service.ProducerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

public class GrpcKafkaServer {
    public static void main(String[] args) {
        try {
            GrpcKafkaServer grpcKafkaServer = new GrpcKafkaServer();
            grpcKafkaServer.start();
        } catch (Exception e) {
            System.err.println(e);
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

        System.out.println("Listening on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down server");
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
