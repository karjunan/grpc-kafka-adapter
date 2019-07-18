package com.grpc.server.service;

import com.grpc.server.infrastructure.KafkaPersistance;
import com.grpc.server.infrastructure.MessagePersistance;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.Properties;
import java.util.logging.Logger;


public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    private static final Logger logger = Logger.getLogger(ProducerService.class.getName());

    private Properties properties;
//    private static final String TOPIC_NAME = "topic_name";
//    private static final String AVRO_SCHEMA = "avroSchema";



    public ProducerService(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {
        MessagePersistance persistance = new KafkaPersistance();
        persistance.save(request,properties,responseObserver);
    }


    //    @Override
//    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {
//
//        Descriptors.FieldDescriptor topicDescriptor = request.getDescriptorForType().findFieldByName(TOPIC_NAME);
//        Descriptors.FieldDescriptor avroSchemaDescriptor = request.getDescriptorForType().findFieldByName(AVRO_SCHEMA);
//
//        if( !request.hasField(topicDescriptor) || request.getTopicName().length() == 0 ) {
//            Exception ex = new Exception("Topic name cannot be null");
//            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
//                    .augmentDescription("Custom exception")
//                    .withCause(ex)
//                    .asRuntimeException());
//            return;
//        }
//
//        if( !request.hasField(avroSchemaDescriptor) || request.getAvroSchema().length() == 0 ) {
//            Exception ex = new Exception("Avro schema or schema name has to be provided");
//            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
//                    .augmentDescription("Custom exception")
//                    .withCause(ex)
//                    .asRuntimeException());
//            return;
//        }
//
//        try(KafkaProducer<String,GenericRecord> producer = new KafkaProducer<>(properties)){
//
//            GenericRecord record = getAvroRecord(request);
//            Headers headers = getRecordHaders(request.getHeader());
//            ProducerRecord<String,GenericRecord> producerRecord = new ProducerRecord<>
//                    (request.getTopicName(),request.getPartition(),request.getKey(),record,headers);
//
//            logger.info("Message to be  Persisted => " + producerRecord.toString());
//            producer.send(producerRecord);
//            logger.info("Message Persisted " + producerRecord.value());
//            Messages.OkResponse response =
//                    Messages.OkResponse.newBuilder()
//                            .setIsOk(true)
//                            .build();
//            responseObserver.onNext(response);
//            responseObserver.onCompleted();
//            return;
//        } catch (Exception ex) {
//            logger.severe("Exception while persisting data - " + ex.getMessage());
//            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage())
//                    .augmentDescription("Custom exception")
//                    .withCause(ex)
//                    .asRuntimeException());
//        }
//    }

    private Headers getRecordHaders(Messages.Header protoHeader) {
        Headers headers = new RecordHeaders();
        protoHeader.getPairsMap().entrySet()
                .forEach(k -> {
                    Header header = new RecordHeader(k.getKey(),k.getValue().getBytes());
                    headers.add(header);
                });
        return headers;
    }

    private GenericRecord getAvroRecord(Messages.ProducerRequest request) {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(request.getAvroSchema());
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("value",request.getValue());
        return avroRecord;
    }
}
