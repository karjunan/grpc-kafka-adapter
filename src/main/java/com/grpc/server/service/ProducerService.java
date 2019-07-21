package com.grpc.server.service;

import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.util.Utils;
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
    private KafkaProducerConfig kafkaProducerConfig;

    private static final String AVRO_SCHEMA = "avroSchema";

    @Override
    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {


        if (request.getTopicList().isEmpty()) {
            Exception ex = new Exception("Topic name missing");
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription("Topic name missing")
                    .withCause(ex)
                    .asRuntimeException());
            return;
        }

        if (request.getHeader().getPairsMap().size() == 0 ||
                !request.getHeader().getPairsMap().containsKey(AVRO_SCHEMA) ||
                !request.getHeader().getPairsMap().containsKey("correlationId")) {
            responseObserver.onError(Status.CANCELLED
                    .withDescription("Missing Mandataory headers")
                    .asRuntimeException());
            return;
        }

        try {
            kafkaProducerConfig.kafkaTemplate().executeInTransaction(template -> {
                for (String topic : request.getTopicList()) {
                    ProducerRecord<String, GenericRecord> producerRecord = new ProducerRecord<>
                            (topic, request.getPartition(), request.getKey(), Utils.getAvroRecord(request),
                                    Utils.getRecordHaders(request.getHeader()));
                    template.send(producerRecord);

                }
                return null;
            });

            Messages.OkResponse response = Messages.OkResponse.newBuilder()
                    .setIsOk(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        } catch (Exception ex) {
            log.error("Exception while persisting data - " + ex);
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription(ex.getCause().getMessage())
                    .withCause(ex.getCause())
                    .asRuntimeException());
        }

    }

}
