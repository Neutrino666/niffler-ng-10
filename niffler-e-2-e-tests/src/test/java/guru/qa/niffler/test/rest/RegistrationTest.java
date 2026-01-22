package guru.qa.niffler.test.rest;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.service.user.AuthApiClient;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public class RegistrationTest {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
  void newUserShouldRegisteredByApiCall() throws IOException {
    final Response<Void> response = authApiClient.register(RandomDataUtils.getRandomUserName(),
        "12345");
    Assertions.assertEquals(201, response.code());
  }
}
