package com.grpc.server.service;

import com.grpc.server.config.properties.KafkaConsumerProperties;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Queue;

@Log4j
@Service
public class ConsumerStreamService extends KafkaServiceGrpc.KafkaServiceImplBase {

//    @Autowired
//    private KafkaConsumerProperties kafkaConsumerProperties;
//
//    private static Queue<String> queue = new ArrayDeque<>();
//
//    @Override
//    public void getAll(Messages.GetAllMessages request, StreamObserver<Messages.Response> responseObserver) {
////        System.out.println(WordCountProcessorApplication.queue);
//    }
////
//    @EnableBinding(Sink.class)
//    public static class MessageProcessor {
//
//        @StreamListener(Sink.INPUT)
//        public void process(Event event) {
//            System.out.println("Getting Value Data " + event.getValue());
//        }
//    }
//
//    public interface Sink {
//        String INPUT = "t1";
//
//        @Input(Sink.INPUT)
//        SubscribableChannel input();
//    }
//
//    public class Event {
//
//        private  String value;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//    }
}
