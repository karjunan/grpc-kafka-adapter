package com.grpc.server.service.consumer;

import com.grpc.server.config.KafkaStreamsConfig;
import com.grpc.server.config.properties.KafkaConsumerProperties;
import com.grpc.server.proto.KafkaConsumerServiceGrpc;
import com.grpc.server.proto.MessagesConsumer;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j
@Service
@ConditionalOnProperty(name = "consumerBinding", havingValue = "true")
public class ConsumerStreamService extends KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase {


    private Queue<String> queue = new LinkedBlockingQueue<>();

    @Autowired
    public ConsumerStreamService(KafkaConsumerProperties kafkaConsumerProperties,
                                 KafkaStreamsConfig kafkaStreamsConfig) {
        StreamsBuilder nameAgeBuilder = new StreamsBuilder();
        System.out.println("Current properties => " + kafkaConsumerProperties);

        KStream<String, String> name = nameAgeBuilder.stream(kafkaConsumerProperties.getTopic(), Consumed.with(Serdes.String(), Serdes.String()));
        name.foreach((k,v) -> {
            System.out.println(v);
            synchronized (queue){
                queue.add(v);
            }

        });
        KafkaStreams kafkaStreams = new KafkaStreams(nameAgeBuilder.build(),kafkaStreamsConfig.config());
        kafkaStreams.start();
    }



    @Override
    public void getAll(MessagesConsumer.GetAllMessages request, StreamObserver<MessagesConsumer.Response> responseObserver) {

        try {
            while(true){
                synchronized (queue) {
                    queue.forEach(v -> {
                        System.out.println(queue);
                        MessagesConsumer.Event event = MessagesConsumer.Event.newBuilder().setValue(v).build();
                        responseObserver.onNext(MessagesConsumer.Response.newBuilder().setEvent(event).build());
                        queue.poll();
                    });
                }

            }

        } catch (Exception ex) {
            responseObserver.onError(Status.CANCELLED
                    .withDescription("Something went wrong while processing the request. Please restart !!")
                    .asRuntimeException());
            return;
        }
    }


}
