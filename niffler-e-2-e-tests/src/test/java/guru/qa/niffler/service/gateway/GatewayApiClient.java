package guru.qa.niffler.service.gateway;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.rest.GatewayApi;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public final class GatewayApiClient extends RestClient {

  private final GatewayApi gatewayApi;

  public GatewayApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayApi = create(GatewayApi.class);
  }

  @Nonnull
  @Step("REST API Получение всех входящих заявок")
  public List<UserJson> allFriends(String bearerToken, String searchQuery) {
    final Response<List<UserJson>> response;
    try {
      response = gatewayApi.allFriends(bearerToken, searchQuery)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body() != null ? response.body() : List.of();
  }
}