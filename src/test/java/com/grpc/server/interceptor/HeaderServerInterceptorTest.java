package com.grpc.server.interceptor;

import io.grpc.Channel;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HeaderServerInterceptorTest {

    private Channel channel;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();


//    @Before
//    public void setUp() throws Exception {
//        ProducerService producerService =
//                new ProducerService() {
//                    @Override
//                    public void save(Messages.Request request, StreamObserver<Messages.OkResponse> responseObserver) {
//                        responseObserver.onNext(Messages.OkResponse.getDefaultInstance());
//                        responseObserver.onCompleted();
//                    }
//                };
//
//        // Generate a unique in-process server name.
//        String serverName = InProcessServerBuilder.generateName();
//        // Create a server, add service, start, and register for automatic graceful shutdown.
//        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor()
//                .addService(ServerInterceptors.intercept(producerService, new HeaderServerInterceptor()))
//                .build().start());
//
//        // Create a client channel and register for automatic graceful shutdown.
//
//        channel =
//                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
//    }
//
//    @Test
//    public void serverHeaderReadAndValidate() {
//        class SpyingClientInterceptor implements ClientInterceptor {
//            ClientCall.Listener<?> spyListener;
//
//            @Override
//            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
//                    MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
//                return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
//                    @Override
//                    public void start(Listener<RespT> responseListener, Metadata headers) {
//                        headers.put(Metadata.Key.of("correlation_id",Metadata.ASCII_STRING_MARSHALLER),"123456");
//                        spyListener = responseListener =
//                                Mockito.mock(ClientCall.Listener.class, AdditionalAnswers.delegatesTo(responseListener));
//                        super.start(responseListener, headers);
//                    }
//                };
//            }
//        }
//
//        SpyingClientInterceptor clientInterceptor = new SpyingClientInterceptor();
//        KafkaServiceGrpc.KafkaServiceBlockingStub blockingStub = KafkaServiceGrpc.newBlockingStub(channel)
//                .withInterceptors(clientInterceptor);
//        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
//
//        blockingStub.save(Messages.Request.getDefaultInstance());
//
//        Assert.assertNotNull(clientInterceptor.spyListener);
//        Mockito.verify(clientInterceptor.spyListener).onHeaders(metadataCaptor.capture());
////        Assert.assertEquals(
////                "correlation_id",
////                metadataCaptor.getValue().get(HeaderServerInterceptor.CORRELATION_ID));
//    }


}
