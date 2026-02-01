package guru.qa.niffler.test.rest;

import static org.assertj.core.api.Assertions.assertThat;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.AuthApiClient;
import guru.qa.niffler.service.user.UserClient;
import guru.qa.niffler.service.user.UsersApiClient;
import io.qameta.allure.Step;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

@ParametersAreNonnullByDefault
@DisplayName("REST Registration")
public final class RegistrationTest {

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final UserClient userClient = new UsersApiClient();

  @Test
  @Step("Регистрация нового пользователя")
  void newUserShouldRegisteredByApiCall() throws IOException {
    final Response<Void> response = authApiClient.register(RandomDataUtils.getRandomUserName(),
        "12345");
    Assertions.assertEquals(201, response.code());
  }

  @Test
  @Step("Создание нового пользователя")
  void newUserShouldCreatedByApi() {
    String username = RandomDataUtils.getRandomUserName();
    UserJson user = userClient.create(username, UserExtension.DEFAULT_PASSWORD);
    assertThat(user)
        .extracting(UserJson::username)
        .isEqualTo(username);
  }
}
