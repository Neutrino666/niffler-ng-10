package guru.qa.niffler.test.rest;

import static guru.qa.niffler.helpers.OAuth2Utils.generateCodeChallenge;
import static guru.qa.niffler.helpers.OAuth2Utils.generateCodeVerifier;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.OAuth2ApiClient;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OAuth2")
public class FakeLoginTest {

  private final OAuth2ApiClient OAuth2ApiClient = new OAuth2ApiClient();

  @Test
  @User
  @DisplayName("Получение токена")
  void oAuth2Test(UserJson user) throws IOException {
    String codeVerifier = generateCodeVerifier();
    String codeChallenge = generateCodeChallenge(codeVerifier);

    OAuth2ApiClient.preRequest(codeChallenge);
    String code = OAuth2ApiClient.login(user.username(), user.testData().password());
    String token = OAuth2ApiClient.token(code, codeVerifier);

    Assertions.assertThat(token)
        .as("Полученный токен не пустая строка")
        .isNotEmpty()
        .hasSizeGreaterThan(1);
  }
}
