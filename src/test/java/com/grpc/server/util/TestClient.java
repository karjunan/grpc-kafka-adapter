package com.grpc.server.util;

import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Before;
import org.junit.Test;

public class TestClient {

    private KafkaServiceGrpc.KafkaServiceBlockingStub kafkaServiceBlockingStub;


    @Before
    public void init() {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 8000).usePlaintext().build();

        kafkaServiceBlockingStub =
                KafkaServiceGrpc.newBlockingStub(managedChannel);
    }

    @Test
    public void test() {
        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
                .setValue("hello").addTopic("t1").build();

        Messages.OkResponse response = kafkaServiceBlockingStub.save(producerRequest);
        System.out.println(response);
    }
}
