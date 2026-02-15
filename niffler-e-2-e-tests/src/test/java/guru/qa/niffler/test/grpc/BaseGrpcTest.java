package guru.qa.niffler.test.grpc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.jupiter.meta.GrpcTest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@GrpcTest
public class BaseGrpcTest {

  public static final Config CFG = Config.getInstance();

  protected static final Channel channel = ManagedChannelBuilder
      .forAddress(CFG.currencyGrpcAddress(), CFG.currencyGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  protected static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub blockingStub
      = NifflerCurrencyServiceGrpc.newBlockingStub(channel);

}