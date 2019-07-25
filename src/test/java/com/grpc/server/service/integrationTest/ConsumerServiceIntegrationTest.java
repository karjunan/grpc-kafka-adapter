package com.grpc.server.service.integrationTest;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.proto.KafkaConsumerServiceGrpc;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.MessagesConsumer;
import com.grpc.server.service.consumer.ConsumerStreamService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
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

    private KafkaConsumerServiceGrpc.KafkaConsumerServiceBlockingStub kafkaConsumerServiceBlockingStub;

    KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = null;

    @Before
    public void init() throws Exception {

        KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase implBase =
                new KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase() {
                    @Override
                    public void getAll(MessagesConsumer.GetAllMessages request, StreamObserver<MessagesConsumer.Response> responseObserver) {

                    }
                };
    }

    @Test
    public void test() {
//        kafkaConsumerServiceBlockingStub.getAll(MessagesConsumer.GetAllMessages.newBuilder().build());

    }
}
