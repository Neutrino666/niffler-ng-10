package guru.qa.niffler.service.gateway;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public final class GatewayV2ApiClient extends RestClient {

  private final GatewayV2Api gatewayApi;

  public GatewayV2ApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayApi = create(GatewayV2Api.class);
  }

  @Nonnull
  @Step("REST API GET api/v2/friends/all")
  public RestResponsePage<UserJson> allFriends(String bearerToken,
      int page,
      int size,
      @Nullable List<String> sort,
      @Nullable String searchQuery) {
    final Response<RestResponsePage<UserJson>> response;
    try {
      response = gatewayApi.allFriends(bearerToken, page, size, sort, searchQuery)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body() != null ? response.body() : new RestResponsePage<>(null);
  }
}