package com.grpc.server.service.integrationTest;

import com.grpc.server.avro.Message;
import com.grpc.server.service.consumer.ConsumerStreamService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("consumer-test")
@SpringBootTest
public class ConsumerServiceIntegrationTest {

    @Before
    public void setup() {

    }

    @Test
    public void testUpperCase() {
        String val = "hello";
        Message message = Message.newBuilder()
                .setValue(val).build();
       ConsumerStreamService.MessageProcessor.processMessage(message);
    }

}
