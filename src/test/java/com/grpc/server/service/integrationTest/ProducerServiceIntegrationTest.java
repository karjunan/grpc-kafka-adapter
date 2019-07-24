package com.grpc.server.service.integrationTest;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.service.producer.ProducerService;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@ActiveProfiles("producer-test")
@SpringBootTest
public class ProducerServiceIntegrationTest {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private HeaderServerInterceptor headerServerInterceptor;

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

        Map<String,String> headers = new HashMap<>();
        headers.put("correlationId", "1234");
        headers.put("transcationId", "5678");
        headers.put("avroSchema",UtilHelper.getAvroData());

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("t1")
                .addTopic("t2")
//                .addTopic("t3")
                .setValue("Entering Kafka")
                .putAllHeader(headers)
                .build();
    }

    @Test
    public void test() {
       for(int i = 0; i < 10; i++) {
            Messages.OkResponse response = blockingStub.save(producerRequest);

        }
//        Assert.assertEquals(true,response.getIsOk());
//        System.out.println(response);
    }
}
