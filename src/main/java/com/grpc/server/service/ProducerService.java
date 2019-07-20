package com.grpc.server.service;

import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j
@Service
public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    @Autowired
    KafkaProducerConfig kafkaProducerConfig;

    @Override
    public void save(Messages.ProducerRequest request, StreamObserver<Messages.OkResponse> responseObserver) {
//        MessagePersistance persistance = new KafkaPersistanceImpl();
//        env.
//        persistance.save(request,responseObserver);
        System.out.println("Topic Name => " + kafkaProducerConfig.kafkaTemplate().getDefaultTopic());
        responseObserver.onCompleted();
    }

}
