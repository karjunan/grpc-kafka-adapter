package com.grpc.server.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Message;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;


public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    private static final Logger logger = Logger.getLogger(ProducerService.class.getName());

    private Properties properties;
    private TestHelper testHelper;

    public ProducerService(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {

        if(Objects.isNull(request.getTopicName())) {
            responseObserver.onError(new Exception(" Topic name cannot be null"));
            return;
        }
        System.out.println("Properrties => " + properties);
        try(KafkaProducer<String,GenericRecord> producer = new KafkaProducer<>(properties)){

            GenericRecord record = avroRecord(request);
            System.out.println(record);
            ProducerRecord<String,GenericRecord> producerRecord = new ProducerRecord<>(request.getTopicName(),record);
            producer.send(producerRecord);
            logger.info("Message Persisted => " + producerRecord.toString());
            Messages.OkResponse response =
                    Messages.OkResponse.newBuilder()
                            .setIsOk(true)
                            .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            if(testHelper != null ) {
                testHelper.onMessage(response);
            }
            return;
        } catch (Exception ex) {
            logger.severe("Exception while persisting data - " + ex.getMessage());
            if(testHelper != null ) {
                testHelper.onRpcError(new Exception(ex.getMessage()));
            }
            responseObserver.onError(ex);
        }
    }

    private GenericRecord avroRecord(Messages.ProducerRequest request) throws IOException {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(request.getAvroSchemaFormat());
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("value",request.getValue());
        avroRecord.put("header",request.getHeaderMap());
        return avroRecord;
    }

    @VisibleForTesting
    interface TestHelper {
        /**
         * Used for verify/inspect message received from server.
         */
        void onMessage(Message message);

        /**
         * Used for verify/inspect error received from server.
         */
        void onRpcError(Throwable exception);
    }

    @VisibleForTesting
    void setTestHelper(TestHelper testHelper) {
        this.testHelper = testHelper;
    }
}
