package com.grpc.server.service.unitTest;

import com.grpc.server.interceptor.HeaderServerInterceptor;
import com.grpc.server.proto.KafkaConsumerServiceGrpc;
import com.grpc.server.proto.KafkaServiceGrpc;
import com.grpc.server.proto.MessagesConsumer;
import com.grpc.server.service.consumer.ConsumerStreamService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.util.MutableHandlerRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(SpringRunner.class)
@ActiveProfiles("consumer-test")
@SpringBootTest
public class ConsumerServiceTest {

    @Autowired
    private ConsumerStreamService consumerStreamService;

    @Autowired
    private HeaderServerInterceptor headerServerInterceptor;


    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private KafkaConsumerServiceGrpc.KafkaConsumerServiceBlockingStub kafkaConsumerServiceBlockingStub;


    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();

    TestClient testClient;

    @Before
    public void init() throws Exception {
        String serverName = InProcessServerBuilder.generateName();


        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .fallbackHandlerRegistry(serviceRegistry).directExecutor().build().start());

        testClient = new TestClient(channel);


    }

    @After
    public void tearDown() throws Exception {
        testClient.shutdown();
    }


    @Test
    public void test() {
        MessagesConsumer.Response res = MessagesConsumer.Response.newBuilder().setEvent(MessagesConsumer.Event.newBuilder()
                .setValue("Value-2").build()).build();
        Queue<String> queue = new LinkedBlockingQueue<>();
        queue.add("Value-1");
        queue.add(("Value-2"));
        Queue<MessagesConsumer.Response> outputQueue = new LinkedBlockingQueue<>();
        final AtomicReference<MessagesConsumer.Response> reference = new AtomicReference<>();

        KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase implBase =
                new KafkaConsumerServiceGrpc.KafkaConsumerServiceImplBase() {
                    @Override
                    public void getAll(MessagesConsumer.GetAllMessages request, StreamObserver<MessagesConsumer.Response> responseObserver) {
                        queue.forEach(v -> {
                            MessagesConsumer.Response res = MessagesConsumer.Response.newBuilder().setEvent(MessagesConsumer.Event.newBuilder()
                                    .setValue(v).build()).build();
                            reference.set(res);
                            outputQueue.add(res);
                            responseObserver.onNext(res);
                            queue.poll();

                        });
                        responseObserver.onCompleted();
                    }
                };
        serviceRegistry.addService(implBase);
        testClient.getAll();
        Assert.assertEquals(res,reference.get());
        Assert.assertEquals(0, queue.size()) ;

    }

    public static class TestClient {

        private final ManagedChannel channel;
        private final KafkaConsumerServiceGrpc.KafkaConsumerServiceBlockingStub blockingStub;

        /** Construct client connecting to HelloWorld server at {@code host:port}. */
        public TestClient(String host, int port) {
            this(ManagedChannelBuilder.forAddress(host, port)
                    // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                    // needing certificates.
                    .usePlaintext()
                    .build());
        }

        /** Construct client for accessing HelloWorld server using the existing channel. */
        TestClient(ManagedChannel channel) {
            this.channel = channel;
            blockingStub = KafkaConsumerServiceGrpc.newBlockingStub(channel);
        }

        public void shutdown() throws InterruptedException {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }

        /** Say hello to server. */
        public void getAll() {

            MessagesConsumer.GetAllMessages request = MessagesConsumer.GetAllMessages.newBuilder().build();

            try {
                Iterator< MessagesConsumer.Response> response = blockingStub.getAll(request);
            } catch (StatusRuntimeException e) {
                e.printStackTrace();
                return;
            }
        }

        /**
         * Greet server. If provided, the first element of {@code args} is the name to use in the
         * greeting.
         */
        public static void main(String[] args) throws Exception {
            TestClient client = new TestClient("localhost", 50051);
            try {
                /* Access a service running on the local machine on port 50051 */
                String user = "world";
                if (args.length > 0) {
                    user = args[0]; /* Use the arg as the name to greet if provided */
                }
                client.getAll();
            } finally {
                client.shutdown();
            }
        }
    }


}

