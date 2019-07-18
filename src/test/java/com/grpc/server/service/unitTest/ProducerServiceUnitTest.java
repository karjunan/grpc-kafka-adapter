package com.grpc.server.service.unitTest;

import com.grpc.server.proto.Messages;
import com.grpc.server.service.ProducerService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class ProducerServiceUnitTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private List<String> serverList = new ArrayList<>();
    private List<String> schemaRegistryList = new ArrayList<>();

    private Properties  properties = new Properties();
    private final String server = "localhost:9092";
    private String schemaRegistry = "http://localhost:8081";


    @Before
    public void setup() {
        serverList.add(server);
        schemaRegistryList.add(schemaRegistry);
        final String BOOTSTRAP_SERVERS = serverList.stream().collect(Collectors.joining(","));
        final String SCHEMA_REGISTRIES = schemaRegistryList.stream().collect(Collectors.joining(","));
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS );
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class.getName());
        properties.put("auto.register.schemas", false);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRIES);
    }

    @Test
    public void save_message_failed_when_avro_schema_not_present() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new ProducerService(properties)).build().start());

        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("corelationId","1234")
                .putPairs("transcationId","5678")
                .build();

        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
                .setTopicName("topic-1")
                .setValue("Hai from grpc")
//                .setAvroSchema(getAvroData())
                .setHeader(header)
                .build();

        thrown.expect(StatusRuntimeException.class);
        thrown.expectMessage("Avro schema or schema name has to be provided");
        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        reply.getIsOk();
    }

    @Test
    public void save_message_failed_when_topic_name_not_present() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new ProducerService(properties)).build().start());

        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("corelationId","1234")
                .putPairs("transcationId","5678")
                .build();

        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
//                .setTopicName("topic-1")
                .setValue("Hai from grpc")
                .setAvroSchema(getAvroData())
                .setHeader(header)
                .build();

        thrown.expect(StatusRuntimeException.class);
        thrown.expectMessage("Topic name cannot be null");
        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        reply.getIsOk();
    }

    @Test
    public void save_message_failed_when_properties_not_present() throws Exception {

        properties = new Properties();
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new ProducerService(properties)).build().start());

        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("corelationId","1234")
                .putPairs("transcationId","5678")
                .build();

        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
                .setTopicName("topic-1")
                .setValue("Hai from grpc")
                .setAvroSchema(getAvroData())
                .setHeader(header)
                .build();

        thrown.expect(CoreMatchers.anything("Exception while persisting data"));
        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        reply.getIsOk();
    }

//    @Test
//    public void save_message_failed_when_properties_not_present() throws Exception {
//
//        properties = new Properties();
//        // Generate a unique in-process server name.
//        String serverName = InProcessServerBuilder.generateName();
//
//        // Create a server, add service, start, and register for automatic graceful shutdown.
//        grpcCleanup.register(InProcessServerBuilder
//                .forName(serverName).directExecutor().addService(new ProducerService(properties)).build().start());
//
//        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
//                // Create a client channel and register for automatic graceful shutdown.
//                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
//
//
//        Messages.Header header = Messages.Header.newBuilder()
//                .putPairs("corelationId","1234")
//                .putPairs("transcationId","5678")
//                .build();
//
//        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
//                .setTopicName("topic-1")
//                .setValue("Hai from grpc")
//                .setAvroSchema(getAvroData())
//                .setHeader(header)
//                .build();
//
//        thrown.expect(CoreMatchers.anything("Exception while persisting data"));
//        Messages.OkResponse reply =  blockingStub.save(producerRequest);
//        reply.getIsOk();
//    }

    private String getAvroData() throws Exception {
        return Files.lines(Paths.get("src","main","resources","message.avsc"))
                .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));
    }
}
