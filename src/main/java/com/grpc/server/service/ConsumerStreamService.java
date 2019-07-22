package com.grpc.server.service;

import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.service.consumers.StreamConsumer;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;

@Log4j
public class ConsumerStreamService extends KafkaServiceGrpc.KafkaServiceImplBase {

    @Autowired
    StreamConsumer streamConsumer;

    @Override
    public void getAll(Messages.GetAllMessages request, StreamObserver<Messages.Response> responseObserver) {

    }

    @EnableBinding(TestSink.class)
    static class TestConsumer {

        @StreamListener(TestSink.INPUT)
        public void receive(String data) {
            log.info("Man this is unbeliavebale " + data);
        }
    }

    interface TestSink {

        String INPUT = "t1";

        @Input(INPUT)
        SubscribableChannel input();

    }

}
