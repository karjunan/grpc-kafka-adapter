package com.grpc.server.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HeaderServerInterceptor  implements ServerInterceptor {


    static final Metadata.Key<String> CORRELATION_ID =
            Metadata.Key.of("correlation_id", Metadata.ASCII_STRING_MARSHALLER);


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall (
            ServerCall<ReqT, RespT> serverCall,
            final Metadata md,
            ServerCallHandler<ReqT, RespT> next) {
        log.info("header received from client:" + md.keys());
        log.info("Correlation id => " + md.get(CORRELATION_ID));
        return next.startCall(serverCall,md);
    }
}