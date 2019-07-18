package com.grpc.server.infrastructure;

import com.google.protobuf.Descriptors;
import com.grpc.server.proto.Messages;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;
import java.util.logging.Logger;

public class KafkaPersistance implements MessagePersistance {

    private static final Logger logger = Logger.getLogger(KafkaPersistance.class.getName());

    private Properties properties;
    private static final String TOPIC_NAME = "topic_name";
    private static final String AVRO_SCHEMA = "avroSchema";

    @Override
    public void save(Messages.ProducerRequest request, Properties properties,
            StreamObserver<Messages.OkResponse> responseObserver) {

        Descriptors.FieldDescriptor topicDescriptor = request.getDescriptorForType().findFieldByName(TOPIC_NAME);
        Descriptors.FieldDescriptor avroSchemaDescriptor = request.getDescriptorForType().findFieldByName(AVRO_SCHEMA);

        if( !request.hasField(topicDescriptor) || request.getTopicName().length() == 0 ) {
            Exception ex = new Exception("Topic name cannot be null");
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription("Custom exception")
                    .withCause(ex)
                    .asRuntimeException());
            return;
        }

        if( !request.hasField(avroSchemaDescriptor) || request.getAvroSchema().length() == 0 ) {
            Exception ex = new Exception("Avro schema or schema name has to be provided");
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription("Custom exception")
                    .withCause(ex)
                    .asRuntimeException());
            return;
        }

        try(KafkaProducer<String,GenericRecord> producer = new KafkaProducer<>(properties)) {

            // call the generic implementation
            saveDefault(request,producer);

            Messages.OkResponse response =
                    Messages.OkResponse.newBuilder()
                            .setIsOk(true)
                            .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        } catch (Exception ex) {
            logger.severe("Exception while persisting data - " + ex.getMessage());
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
                    .augmentDescription("Custom exception")
                    .withCause(ex)
                    .asRuntimeException());
        }

    }
}
