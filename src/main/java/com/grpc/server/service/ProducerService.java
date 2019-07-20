package com.grpc.server.service;

import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.infrastructure.MessagePersistance;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j
@Service
public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    @Autowired
    KafkaProducerConfig kafkaProducerConfig;

    @Override
    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {

        try {
            for (String topic : request.getTopicList()) {
                ProducerRecord producerRecord = new ProducerRecord
                        (topic, request.getPartition(), request.getKey(), MessagePersistance.getAvroRecord(request),
                                MessagePersistance.getRecordHaders(request.getHeader()));
                log.info(" Producer Record => " +  MessagePersistance.getAvroRecord(request));
                kafkaProducerConfig.kafkaTemplate().send(producerRecord);
            }
            System.out.println("Topic Name => " + kafkaProducerConfig);
            Messages.OkResponse response = Messages.OkResponse.newBuilder()
                    .setIsOk(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        } catch (Exception ex) {
            log.error("Exception while persisting data - " + ex);
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription("Custom exception")
                    .withCause(ex)
                    .asRuntimeException());
        }

    }

}
