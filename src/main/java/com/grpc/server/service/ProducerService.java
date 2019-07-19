package com.grpc.server.service;

import com.grpc.server.infrastructure.KafkaPersistance;
import com.grpc.server.infrastructure.MessagePersistance;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;

import java.util.Properties;

@Log4j
public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    private Properties properties;

    public ProducerService(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {
        MessagePersistance persistance = new KafkaPersistance();
        persistance.save(request,properties,responseObserver);
    }

}
