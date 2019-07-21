package com.grpc.server.service.unitTest;

import com.grpc.server.config.KafkaProducerConfig;
import com.grpc.server.config.KafkaProducerProperties;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.Messages;
import com.grpc.server.service.ProducerService;
import com.grpc.server.util.UtilHelper;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ProducerService.class,KafkaProducerConfig.class,KafkaProducerProperties.class})
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



        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("correlationId","1234")
                .putPairs("transcationId","5678")
                .putPairs("avroSchema",UtilHelper.getAvroData())
                .build();

        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .setHeader(header)
                .build();

    }

    @Test
    public void save_message_successfully()  {

        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        Messages.OkResponse reply =  blockingStub.save(producerRequest);
        Assert.assertEquals(true,reply.getIsOk());

    }

    @Test
    public void save_message_throw_missing_header_when_missing_avro_schema()  {

        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("correlationId","1234")
                .putPairs("transcationId","5678")
//                .putPairs("avroSchema",UtilHelper.getAvroData())
                .build();

        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .setHeader(header)
                .build();

//        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        exception.expect(StatusRuntimeException.class);
        exception.expectMessage("Missing Mandataory headers");
        blockingStub.save(producerRequest);

    }

    @Test
    public void save_message_throw_missing_header_when_correlationId_not_present() throws Exception {

        Messages.Header header = Messages.Header.newBuilder()
//                .putPairs("correlationId","1234")
                .putPairs("transcationId","5678")
                .putPairs("avroSchema",UtilHelper.getAvroData())
                .build();

        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .setHeader(header)
                .build();

//        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        exception.expect(StatusRuntimeException.class);
        exception.expectMessage("Missing Mandataory headers");
        blockingStub.save(producerRequest);

    }


    @Test
    public void save_message_throw_missing_topic__when_topic_not_present() throws Exception {

        Messages.Header header = Messages.Header.newBuilder()
                .putPairs("correlationId","1234")
                .putPairs("transcationId","5678")
                .putPairs("avroSchema",UtilHelper.getAvroData())
                .build();

        List<String> list = new ArrayList<>();
        list.add("topic-1");

        producerRequest = Messages.ProducerRequest.newBuilder()
//                .addTopic("topic-1")
//                .addTopic("topic-2")
                .setValue("Finally its working")
                .setHeader(header)
                .build();

//        Mockito.when(template.send(Mockito.any(ProducerRecord.class))).thenReturn(Mockito.any());
        exception.expect(StatusRuntimeException.class);
        exception.expectMessage("Topic name missing");
        blockingStub.save(producerRequest);

    }


}
