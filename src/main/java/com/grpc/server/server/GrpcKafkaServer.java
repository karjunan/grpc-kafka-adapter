package com.grpc.server.server;

import com.grpc.server.service.ProducerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.support.GenericApplicationContext;

@Log4j
public class GrpcKafkaServer implements ApplicationRunner  {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    private ProducerService producerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        applicationContext.registerBean("productService",ProducerService.class,() -> new ProducerService());
        start();
    }

    private Server server;
    
    private void start() throws Exception {
        final int port = 8000;
        server = ServerBuilder.forPort(port)
                .addService(producerService)
//                .addService(ServerInterceptors.intercept(applicationContext.getBean("productService",ProducerService.class),new HeaderServerInterceptor()))
                .build()
                .start();
        log.info("Listening on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.error("Shutting down server");
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
