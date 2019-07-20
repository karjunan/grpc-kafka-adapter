package com.grpc.server.service.integrationTest;

import com.grpc.server.proto.Messages;
import com.grpc.server.service.ProducerService;
import com.grpc.server.util.CustomAvroSerializer;
import com.grpc.server.util.KafkaGrpcTestServer;
import com.grpc.server.util.ZookeeperGrpcTestServer;
import com.salesforce.kafka.test.KafkaTestServer;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
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


    static final String schemaRegistry = "http://localhost:8081";

//    @ClassRule
//    public static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource();
    KafkaTestServer testServer = null;

    List<String> serverList = new ArrayList<>();
    List<String> schemaRegistryList = new ArrayList<>();

    Properties properties;

   SchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();

    @Before
    public void setup() throws Exception {

        Path pathToBeDeleted = Paths.get("\\tmp");
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(v -> {
                    System.out.println(v);
                    v.delete();
                });

        String server = "localhost:9092";
        serverList.add(server);

        schemaRegistryList.add(schemaRegistry);
        properties = new Properties();
        final String BOOTSTRAP_SERVERS = serverList.stream().collect(Collectors.joining(","));
        final String SCHEMA_REGISTRIES = schemaRegistryList.stream().collect(Collectors.joining(","));
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS );
        properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,CustomAvroSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,CustomAvroSerializer.class.getName());
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRIES);
        ZookeeperGrpcTestServer zookeeperGrpcTestServer = new ZookeeperGrpcTestServer();
        testServer = new KafkaGrpcTestServer(properties,zookeeperGrpcTestServer);
//        schemaRegistryClient.register("topic-1",Message.SCHEMA$);
        testServer.start();
    }

    @After
    public void tearDown() throws Exception {
        testServer.stop();
    }


    @Test
    public void save_message_successfully() throws Exception {

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,testServer.getKafkaConnectString() );
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new ProducerService()).build().start());

        com.grpc.server.proto.KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("corelationId","1234")
                .putPairs("transcationId","5678")
                .build();



        List<String> list = new ArrayList<>();
        list.add("topic-1");

        Messages.ProducerRequest producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setAvroSchema(getAvroData())
                .setValue("Finally its working")
                .setHeader(header)
                .build();

        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        Assert.assertEquals(true,reply.getIsOk());

    }

    private String getAvroData() throws Exception {
        return Files.lines(Paths.get("src","main","resources", "avro/message.avsc"))
                 .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));
    }

}
