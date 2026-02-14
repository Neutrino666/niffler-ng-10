package guru.qa.niffler.test.grpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcConsoleInterceptor implements io.grpc.ClientInterceptor {

  private static final JsonFormat.Printer printer = JsonFormat.printer();

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall(
        channel.newCall(methodDescriptor, callOptions)
    ) {
      @Override
      public void sendMessage(Object message) {
        try {
          String msg = printer.print((MessageOrBuilder) message);
          log.info("REQUEST: {}", msg);
        } catch (InvalidProtocolBufferException e) {
          throw new RuntimeException(e);
        }
        super.sendMessage(message);
      }

      @Override
      public void start(Listener responseListener, Metadata headers) {
        ForwardingClientCallListener<Object> clientCallListener = new ForwardingClientCallListener<>() {

          @Override
          public void onMessage(Object message) {
            // handle response
            try {
              String msg = printer.print((MessageOrBuilder) message);
              log.info("RESPONSE: {}", msg);
            } catch (InvalidProtocolBufferException e) {
              throw new RuntimeException(e);
            }
            super.onMessage(message);
          }

          @Override
          protected Listener<Object> delegate() {
            return responseListener;
          }
        };
        super.start(clientCallListener, headers);
      }
    };
  }
}
