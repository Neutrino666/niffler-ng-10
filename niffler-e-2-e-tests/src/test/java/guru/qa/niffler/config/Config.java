package guru.qa.niffler.config;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Config {

  @Nonnull
  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
  }

  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String authJdbcUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  String userdataUrl();

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String userdataGrpcAddress();

  @Nonnegative
  default int userdataGrpcPort() {
    return 8088;
  }

  @Nonnull
  String spendUrl();

  @Nonnull
  String spendJdbcUrl();

  @Nonnull
  String currencyJdbcUrl();

  @Nonnull
  String currencyGrpcAddress();

  @Nonnegative
  default int currencyGrpcPort() {
    return 8092;
  }

  @Nonnull
  String githubUrl();
}
