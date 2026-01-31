package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.UserJson;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OAuth2")
public class FakeLoginTest {

  @Test
  @ApiLogin(username = "admin", password = "admin")
  @DisplayName("Получение токена")
  void oAuth2Test(@Token String token, UserJson user) {
    System.out.println(user);
    System.out.println(token);
    Assertions.assertThat(token)
        .as("Полученный токен не пустая строка")
        .isNotEmpty()
        .hasSizeGreaterThan(1);
  }
}
