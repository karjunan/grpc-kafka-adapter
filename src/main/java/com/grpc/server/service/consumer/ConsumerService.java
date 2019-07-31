package com.grpc.server.service.consumer;

import com.grpc.server.avro.Message;
import com.grpc.server.config.properties.KafkaConsumerProperties;
import com.grpc.server.interceptor.AvroDeSerializer;
import com.grpc.server.interceptor.ByteArrayToGenericRecordMessageConverter;
import com.grpc.server.proto.KafkaConsumerServiceGrpc;
import com.grpc.server.proto.MessagesConsumer;
import com.grpc.server.util.Utils;
import com.microsoft.applicationinsights.TelemetryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.GenericMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "ConsumerService", havingValue = "true")
public class ConsumerService extends KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase {

    @Autowired
    KafkaConsumerProperties kafkaConsumerProperties;

    @Override
    public void getAll(MessagesConsumer.GetAllMessages request,
                       StreamObserver<MessagesConsumer.Response> responseObserver) {

        DefaultKafkaConsumerFactory<String, GenericRecord> cf =
                new DefaultKafkaConsumerFactory(kafkaConsumerProperties.getConsumerConfiguration()
                ,new StringDeserializer(),new AvroDeSerializer(Message.class));

        if(! Utils.isTopicPresent(request) && request.getTopicCount() == 0) {
            Exception ex = new Exception("Topic name missing");
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription("Topic name missing")
                    .withCause(ex)
                    .asRuntimeException());
            return;
        }
        if(Utils.isHeaderPresent(request)) {
            responseObserver.onError(Status.CANCELLED
                    .withDescription("Missing Mandataory headers")
                    .asRuntimeException());
            return;
        }

//        String [] str = {"t5","t6"};
        String [] str = request.getTopicList().toArray(new String[request.getTopicList().size()]);
        final ContainerProperties containerProperties = new ContainerProperties(str);
        log.info("Configured properties for transactional Consumer => "  + containerProperties);
        final MessageListenerContainer container = new KafkaMessageListenerContainer<>(cf,containerProperties);
        container.setupMessageListener((MessageListener<String,GenericRecord>) req -> {
            log.info("Message Received => " + req.value());

            try {
                responseObserver.onNext(MessagesConsumer.Response.newBuilder()
                        .setEvent(MessagesConsumer.Event.newBuilder().setValue(req.toString()).build()
                        ).build());
                req.value();
            } catch (Exception ex) {
                log.error("Exception while consuming data - " + ex.getCause());
                ex.printStackTrace();
                responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                        .augmentDescription(ex.getCause().getMessage())
                        .withCause(ex.getCause())
                        .asRuntimeException());
                return;
            }

        });
        container.start();

    }



}
