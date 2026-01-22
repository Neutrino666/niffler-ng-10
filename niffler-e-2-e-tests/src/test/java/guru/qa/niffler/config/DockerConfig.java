package guru.qa.niffler.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "";
  }

  @Nonnull
  public String authUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "";
  }

  @Nonnull
  public String gatewayUrl() {
    return "";
  }

  @Nonnull
  public String userdataUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String spendUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String spendJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String currencyJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String githubUrl() {
    return "";
  }
}
