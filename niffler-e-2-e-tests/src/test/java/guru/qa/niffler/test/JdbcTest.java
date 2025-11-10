package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.UserDbClient;
import org.junit.jupiter.api.Test;

public class JdbcTest {

  @Test
  void springJdbcTest() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson user = userDbClient.createUserSpringJdbc(
        new UserJson(
            null,
            "valentin1",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        )
    );

    System.out.println(user);
  }
}
