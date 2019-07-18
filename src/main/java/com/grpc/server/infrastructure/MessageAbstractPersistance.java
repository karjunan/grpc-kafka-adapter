package com.grpc.server.infrastructure;

import com.grpc.server.proto.Messages;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;

import java.util.logging.Logger;

public abstract class MessageAbstractPersistance implements MessagePersistance {

    private static final Logger logger = Logger.getLogger(MessageAbstractPersistance.class.getName());

    @Override
    public void saveDefault(Messages.ProducerRequest request,
                            KafkaProducer<String,GenericRecord> producer) {
            GenericRecord record = MessagePersistance.getAvroRecord(request);
            Headers headers = MessagePersistance.getRecordHaders(request.getHeader());
            ProducerRecord<String,GenericRecord> producerRecord = new ProducerRecord<>
                    (request.getTopicName(),request.getPartition(),request.getKey(),record,headers);

            logger.info("Message to be  Persisted => " + producerRecord.toString());
            producer.send(producerRecord);
            logger.info("Message Persisted " + producerRecord.value());
    }
}
