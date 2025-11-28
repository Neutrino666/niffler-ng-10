package guru.qa.niffler.test.rest;

import guru.qa.niffler.service.user.AuthApiClient;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class RegistrationTest {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
  @Disabled
  void newUserShouldRegisteredByApiCall() throws IOException {
    final Response<Void> response = authApiClient.register("bazz", "12345");
    Assertions.assertEquals(201, response.code());
  }
}
