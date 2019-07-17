package com.grpc.server.service.integrationTest;

import com.grpc.server.proto.Messages;
import com.grpc.server.service.ProducerService;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
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
public class ProducerServiceTest {

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

//    @ClassRule
//    public static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource();
    public static final SharedKafkaTestResource sharedKafkaTestResource = null;

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
    public void save_message_successfully() throws Exception {

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

        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        Assert.assertEquals(true,reply.getIsOk());
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

    private String getAvroData() throws Exception {
        return Files.lines(Paths.get("src","main","resources","message.avsc"))
                 .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));
    }
}
