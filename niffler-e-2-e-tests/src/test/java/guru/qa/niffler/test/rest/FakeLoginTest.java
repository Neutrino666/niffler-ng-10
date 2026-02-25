package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OAuth2")
public class FakeLoginTest extends BaseRestTest {

  @Test
  @User
  @ApiLogin
  @DisplayName("Получение токена")
  void oAuth2Test(@Token String token) {
    Assertions.assertThat(token)
        .as("Полученный токен не пустая строка")
        .isNotEmpty()
        .hasSizeGreaterThan(1);
  }
}
