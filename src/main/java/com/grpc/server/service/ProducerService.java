package com.grpc.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.server.domain.Request;
import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.grpc.stub.StreamObserver;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.logging.Logger;


public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    private static final Logger logger = Logger.getLogger(ProducerService.class.getName());

    static Properties properties;

    public ProducerService() {
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,System.getenv("HOST_IP") );
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class.getName());
    }

    @Override
    public void save(Messages.Request request, StreamObserver<Messages.OkResponse> responseObserver) {

        try(KafkaProducer<String,GenericRecord> producer = new KafkaProducer<>(properties)){
            ProducerRecord<String,GenericRecord> producerRecord = new ProducerRecord<>(System.getenv("TOPIC"),avroRecord(request) );
            producer.send(producerRecord);
            logger.info("Message Persisted => " + producerRecord.toString());
            Messages.OkResponse response =
                    Messages.OkResponse.newBuilder()
                            .setIsOk(true)
                            .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        } catch (Exception ex) {
            logger.severe(() -> ex.getMessage());
        }
        responseObserver.onError(new Exception(" Cannot persist the data " + request.getKey()));
    }

    private GenericRecord avroRecord(Messages.Request request) {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse("message.avsc");
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put(request.getKey(),request.getValue());
        return avroRecord;
    }

}
