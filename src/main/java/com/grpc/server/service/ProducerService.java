package com.grpc.server.service;

import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;


public class ProducerService extends KafkaServiceGrpc.KafkaServiceImplBase {

    @Override
    public void save(Messages.Request request, StreamObserver<Messages.OkResponse> responseObserver) {
        Messages.OkResponse okResponse = Messages.OkResponse.newBuilder().setIsOk(true).build();
        responseObserver.onNext(okResponse);
        responseObserver.onCompleted();
    }

}
