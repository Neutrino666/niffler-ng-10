package guru.qa.niffler.test.grpc;

import guru.qa.niffler.api.grpc.GrpcConsoleInterceptor;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.jupiter.meta.GrpcTest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

@GrpcTest
public class BaseGrpcTest {

  public static final Config CFG = Config.getInstance();

  protected static final Channel currencyChannel = getChannelChannelGrpc(
      CFG.currencyGrpcAddress(),
      CFG.currencyGrpcPort()
  );

  protected static final Channel userdataChannel = getChannelChannelGrpc(
      CFG.userdataGrpcAddress(),
      CFG.userdataGrpcPort()
  );

  protected static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub
      currencyBlockingStub = NifflerCurrencyServiceGrpc.newBlockingStub(currencyChannel);

  protected static final NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub
      userdataBlockingStub = NifflerUserdataServiceGrpc.newBlockingStub(userdataChannel);

  private static Channel getChannelChannelGrpc(String address, int port) {
    return ManagedChannelBuilder
        .forAddress(address, port)
        .intercept(new GrpcConsoleInterceptor())
        .intercept(new AllureGrpc())
        .usePlaintext()
        .build();
  }
}