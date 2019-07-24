package com.grpc.server.service.unitTest;

import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.config.properties.KafkaProducerProperties;
import com.grpc.server.config.properties.GeneralProperties;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.server.GrpcServer;
import com.grpc.server.service.ProducerService;
import com.grpc.server.util.UtilHelper;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ProducerService.class,KafkaProducerConfig.class,GeneralProperties.class})
@ActiveProfiles("test")
public class ProducerServiceTest {

    @Value( "${grpc.port}" )
    private String port;

    @Autowired
    ProducerService producerService;

    @Autowired
    KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    KafkaProducerProperties kafkaProducerProperties;

    @MockBean
    ProducerFactory<String,GenericRecord> factory;

    @MockBean
    KafkaTemplate template;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = null;

    Messages.ProducerRequest producerRequest = null;

    @Before
    public void setup() throws Exception {

        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(producerService).build().start());

        blockingStub = com.grpc.server.proto.KafkaServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));



        Map<String,String> headers = new HashMap<>();
        headers.put("correlationId", "1234");
        headers.put("transcationId", "5678");
        headers.put("avroSchema",UtilHelper.getAvroData());


        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .putAllHeader(headers)
                .build();

    }

    @Test
    public void save_message_successfully()  {

        Mockito.when(template.executeInTransaction(Mockito.any())).thenReturn(Mockito.any());

        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        Assert.assertEquals(true,reply.getIsOk());
        Mockito.verify(template,Mockito.times(1)).executeInTransaction(Mockito.any());
    }

    @Test
    public void save_message_throw_missing_header_when_missing_avro_schema()  {

        Map<String,String> headers = new HashMap<>();
        headers.put("correlationId", "1234");
        headers.put("transcationId", "5678");
//        headers.put("avroSchema",UtilHelper.getAvroData());

        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .putAllHeader(headers)
                .build();

//        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        exception.expect(StatusRuntimeException.class);
        exception.expectMessage("Missing Mandataory headers");
        blockingStub.save(producerRequest);

    }

    @Test
    public void save_message_throw_missing_header_when_correlationId_not_present() throws Exception {

        Map<String,String> headers = new HashMap<>();
//        headers.put("correlationId", "1234");
        headers.put("transcationId", "5678");
        headers.put("avroSchema",UtilHelper.getAvroData());

        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .putAllHeader(headers)
                .build();

//        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        exception.expect(StatusRuntimeException.class);
        exception.expectMessage("Missing Mandataory headers");
        blockingStub.save(producerRequest);

    }


    @Test
    public void save_message_throw_missing_topic__when_topic_not_present() throws Exception {

        Map<String,String> headers = new HashMap<>();
//        headers.put("correlationId", "1234");
        headers.put("transcationId", "5678");
        headers.put("avroSchema",UtilHelper.getAvroData());


        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
//                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .putAllHeader(headers)
                .build();

//        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        exception.expect(StatusRuntimeException.class);
        exception.expectMessage("Topic name missing");
        blockingStub.save(producerRequest);

    }


}
