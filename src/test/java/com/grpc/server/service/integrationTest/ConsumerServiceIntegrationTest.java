package com.grpc.server.service.integrationTest;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.service.consumer.ConsumerStreamService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("consumer-test")
@SpringBootTest
public class ConsumerServiceIntegrationTest {

    @Autowired
    private ConsumerStreamService consumerStreamService;

    @Autowired
    private HeaderServerInterceptor headerServerInterceptor;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private KafkaServiceGrpc.KafkaServiceBlockingStub kafkaServiceBlockingStub;

    KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = null;

    @Before
    public void init() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(consumerStreamService).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    }

    @Test
    public void test() {
        blockingStub.getAll(  Messages.GetAllMessages.newBuilder().build());

    }
}
