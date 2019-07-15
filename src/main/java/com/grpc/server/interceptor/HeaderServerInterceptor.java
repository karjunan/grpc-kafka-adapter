package com.grpc.server.interceptor;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.logging.Logger;

public class HeaderServerInterceptor  implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(HeaderServerInterceptor.class.getName());

    @VisibleForTesting
    static final Metadata.Key<String> CORRELATION_ID =
            Metadata.Key.of("correlation_id", Metadata.ASCII_STRING_MARSHALLER);


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            final Metadata md,
            ServerCallHandler<ReqT, RespT> next) {
        logger.info("header received from client:" + md.keys());
        logger.info("Correlation id => " + md.get(CORRELATION_ID));
        return next.startCall(serverCall,md);
    }
}