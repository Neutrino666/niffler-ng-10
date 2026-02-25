package guru.qa.niffler.test.db;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.UserDbClient;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ParametersAreNonnullByDefault
public class UserTest {

  private static final UserDbClient USER_DB_CLIENT = new UserDbClient();
  private final String ERROR_MSG = "Not found by username - ";

  @ValueSource(strings = {
      "valentin6"
  })
  @ParameterizedTest
  void createUserTest(String username) {
    UserJson user = USER_DB_CLIENT.create(
        username, "12345"
    );
    System.out.println(user);
  }
}
