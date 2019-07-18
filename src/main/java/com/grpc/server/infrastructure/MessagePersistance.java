package com.grpc.server.infrastructure;

import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.Properties;

@FunctionalInterface
public interface MessagePersistance {

    /*
        Provide custom implementation for save method
    */
    void save(Messages.ProducerRequest request,
                            Properties properties,
                            StreamObserver<Messages.OkResponse> responseObserver);

    /*
        There is a default implementation already provided in the
        abstract class
     */
    default void saveDefault(Messages.ProducerRequest request,
                             KafkaProducer<String,GenericRecord> producer) {
        GenericRecord record = MessagePersistance.getAvroRecord(request);
        Headers headers = MessagePersistance.getRecordHaders(request.getHeader());
        ProducerRecord<String,GenericRecord> producerRecord = new ProducerRecord<>
                (request.getTopicName(),request.getPartition(),request.getKey(),record,headers);

//        logger.info("Message to be  Persisted => " + producerRecord.toString());
        producer.send(producerRecord);
//        logger.info("Message Persisted " + producerRecord.value());
    }

    static Headers getRecordHaders(Messages.Header protoHeader) {
        Headers headers = new RecordHeaders();
        protoHeader.getPairsMap().entrySet()
                .forEach(k -> {
                    Header header = new RecordHeader(k.getKey(),k.getValue().getBytes());
                    headers.add(header);
                });
        return headers;
    }

    static GenericRecord getAvroRecord(Messages.ProducerRequest request) {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(request.getAvroSchema());
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("value",request.getValue());
        return avroRecord;
    }

}
