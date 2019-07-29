package com.grpc.server.service.consumer;

import com.grpc.server.proto.KafkaConsumerServiceGrpc;
import com.grpc.server.proto.MessagesConsumer;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Log4j
@Service
@ConditionalOnProperty(name = "consumerBinding", havingValue = "true")
public class ConsumerService extends KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase {


    private StreamObserver<MessagesConsumer.Response> responseObserver;
    private MessagesConsumer.GetAllMessages request;

    @Override
    public void getAll(MessagesConsumer.GetAllMessages request,
                       StreamObserver<MessagesConsumer.Response> responseObserver) {

        this.request = request;
        this.responseObserver = responseObserver;
    }

    @EnableBinding(Sink.class)
    @Configuration
    public class CloudStreamProcessor {

        @StreamListener(Processor.INPUT)
        public void input(GenericRecord input) {

            try {
                log.debug("Message Read " + input);
                responseObserver.onNext(MessagesConsumer.Response.newBuilder()
                        .setEvent(MessagesConsumer.Event.newBuilder().setValue(input.toString()).build()
                        ).build());
            } catch (Exception ex) {
                log.error("Exception while consuming the records - " + ex.getCause());
                ex.printStackTrace();
                responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                        .augmentDescription(ex.getCause().getMessage())
                        .withCause(ex.getCause())
                        .asRuntimeException());
                return;
            }
        }

    }
}
