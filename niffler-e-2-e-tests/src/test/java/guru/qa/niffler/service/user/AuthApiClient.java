package guru.qa.niffler.service.user;

import guru.qa.niffler.api.rest.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.rest.user.AuthApi;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

  @Nonnull
  private final AuthApi authApi;

  public AuthApiClient() {
    super("https://auth.niffler-stage.qa.guru/", true);
    this.authApi = create(AuthApi.class);
  }

  @Nonnull
  @Step("REST API Регистрация нового пользователя")
  public Response<Void> register(String username, String password) throws IOException {
    authApi.requestRegisterForm().execute();
    return authApi.register(
        username,
        password,
        password,
        ThreadSafeCookieStore.INSTANCE.value("XSRF-TOKEN")
    ).execute();
  }
}
