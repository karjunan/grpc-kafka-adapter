package com.grpc.server.service;

import com.grpc.server.proto.Messages;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class ProducerServiceTest {

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @ClassRule
    public static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource();

    List<String> serverList = new ArrayList<>();
    List<String> schemaRegistryList = new ArrayList<>();

    Properties properties;

    @Before
    public void setup() {
        String server = "localhost:9092";
        serverList.add(server);
        String schemaRegistry = "http://localhost:8081";
        schemaRegistryList.add(schemaRegistry);
        properties = new Properties();
        final String BOOTSTRAP_SERVERS = serverList.stream().collect(Collectors.joining(","));
        final String SCHEMA_REGISTRIES = schemaRegistryList.stream().collect(Collectors.joining(","));
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS );
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class.getName());
        properties.put("auto.register.schemas", false);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRIES);
    }



    @Test
    public void save_replyMessage_test() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

//        System.out.println("Brokers => " + sharedKafkaTestResource.getKafkaBrokers().getBrokerById(1).getConnectString());
        // Create a server, add service, start, and register for automatic graceful shutdown.

        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new ProducerService(properties)).build().start());

        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        System.out.println(getAvroFile().toString());
        MockSchemaRegistryClient mockSchemaRegistryClient = new MockSchemaRegistryClient();
        Schema schema = new Schema.Parser().parse(Paths.get("src","main","resources","message.avsc").toFile());
        mockSchemaRegistryClient.register("message",schema);
        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
                .setTopicName("topic-1")
                .setValue("Hai from krishna")
                .setAvroSchemaFormat(getAvroFile())
                .build();

        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        Assert.assertEquals(true,reply.getIsOk());
    }

    private String getAvroFile() throws Exception {

        return Files.lines(Paths.get("src","main","resources","message.avsc"))
                 .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));

    }


    //    @Test
//    public void save_replyMessage_failed_persistance_test() throws Exception {
//        // Generate a unique in-process server name.
//        String serverName = InProcessServerBuilder.generateName();
//
//        // Create a server, add service, start, and register for automatic graceful shutdown.
//        grpcCleanup.register(InProcessServerBuilder
//                .forName(serverName).directExecutor().addService(new ProducerService()).build().start());
//
//        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
//                // Create a client channel and register for automatic graceful shutdown.
//                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
//
//        Messages.Request request =Messages.Request.newBuilder()
//                .setKey("hello")
//                .setValue("test")
//                .build();
//
//        Messages.OkResponse reply =
//                blockingStub.save(request);
//
//        thrown.expect(Exception.class);
//        reply.getIsOk();
//    }

//    @Test(expected = Exception.class)
//    public void save_kafa_not_up_and_running_test() throws Exception {
//        // Generate a unique in-process server name.
//        String serverName = InProcessServerBuilder.generateName();
//
//        // Create a server, add service, start, and register for automatic graceful shutdown.
//        grpcCleanup.register(InProcessServerBuilder
//                .forName(serverName).directExecutor().addService(new ProducerService()).build().start());
//
//        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
//                // Create a client channel and register for automatic graceful shutdown.
//                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
//
//        Messages.Request request =Messages.Request.newBuilder()
//                .setKey("hello")
//                .setValue("test")
//                .build();
//
//        Messages.OkResponse reply =
//                blockingStub.save(request);
//        reply.getIsOk();
//    }
}
