package com.grpc.server.service.consumer;

import com.grpc.server.proto.KafkaConsumerServiceGrpc;
import com.grpc.server.proto.MessagesConsumer;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "ConsumerService", havingValue = "true")
public class ConsumerService extends KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase {

    @Autowired
    TelemetryClient telemetryClient;

    private StreamObserver<MessagesConsumer.Response> responseObserver;
    private MessagesConsumer.GetAllMessages request;

    @Override
    public void getAll(MessagesConsumer.GetAllMessages request,
                       StreamObserver<MessagesConsumer.Response> responseObserver) {


        //track a custom event
        telemetryClient.trackEvent("Sending a custom event...");

        //trace a custom trace
        telemetryClient.trackTrace("Sending a custom trace....");

        //track a custom metric
        telemetryClient.trackMetric("custom metric", 1.0);

        //track a custom dependency
        telemetryClient.trackDependency("SQL", "Insert", new Duration(0, 0, 1, 1, 1), true);

        this.request = request;
        this.responseObserver = responseObserver;
    }

    @EnableBinding(Sink.class)
    @Configuration
    public class CloudStreamProcessor {

        @StreamListener(Processor.INPUT)
        public void input(GenericRecord input) {
            System.out.println("Input " + input);
            try {
                log.info("Message Read " + input);
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
