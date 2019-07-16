package com.grpc.server.util;

import com.salesforce.kafka.test.KafkaBrokers;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import org.junit.ClassRule;
import org.junit.Test;

public class KafkaTestResource {

    @ClassRule
    public static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource();

    @Test
    public void test() {

        KafkaBrokers brokers = sharedKafkaTestResource.getKafkaBrokers();
        brokers.forEach(v -> System.out.println(v.getBrokerId()));
        sharedKafkaTestResource.getKafkaTestUtils().describeClusterNodes().forEach(v -> System.out.println(v));
    }

}