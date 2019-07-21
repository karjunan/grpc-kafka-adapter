package com.grpc.server.service.integrationTest;

import com.grpc.server.GrpcApplication;
import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.config.KafkaProducerProperties;
import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.server.GrpcServer;
import com.grpc.server.service.ProducerService;
import com.grpc.server.util.UtilHelper;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GrpcServer.class,ProducerService.class,KafkaProducerConfig.class,
        KafkaProducerProperties.class,HeaderServerInterceptor.class,
GrpcApplication.class})
public class ProducerServiceIntegrationTest {

    @Value( "${grpc.port}" )
    private String port;

    @Autowired
    GrpcServer grpcKafkaServer;

    @Autowired
    private ProducerService producerService;

    @Autowired
    HeaderServerInterceptor headerServerInterceptor;


    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    private KafkaServiceGrpc.KafkaServiceBlockingStub kafkaServiceBlockingStub;

    KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = null;
    Messages.ProducerRequest producerRequest = null;

    @Before
    public void init() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(producerService).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("correlationId", "1234")
                .putPairs("transcationId", "5678")
                .putPairs("avroSchema",UtilHelper.getAvroData())
                .build();

       producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("t1")
                .addTopic("t2")
                .setValue("Finally its working")
                .setHeader(header)
                .build();
    }

    @Test
    public void test() {
        Messages.OkResponse response = blockingStub.save(producerRequest);
        Assert.assertEquals(true,response.getIsOk());
//        System.out.println(response);
    }
}
