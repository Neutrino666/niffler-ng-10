package guru.qa.niffler.test;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.category.CategoryDbClient;
import guru.qa.niffler.service.spend.SpendDbClient;
import guru.qa.niffler.service.user.UserDbClient;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JdbcTest {

  private static UserDbClient USER_DB_CLIENT = new UserDbClient();

  @ValueSource(strings = {
      "valentin6"
  })
  @ParameterizedTest
  void createUserTest(String username) {
    UserJson user = USER_DB_CLIENT.create(
        username, "12345"
    );
    USER_DB_CLIENT.addOutcomeInvitation(user, 1);
  }

  @Test
  void findByUsername() {
    System.out.println(USER_DB_CLIENT.findByUsername("admin"));
  }

  @Test
  void findUserById() {
    System.out.println(
        USER_DB_CLIENT.findById(UUID.fromString("1fbc301c-a629-49ad-9e9f-d7aad0a8c828")));
  }

  @Test
  void deleteUser() {
    USER_DB_CLIENT.delete(
        new UserJson(
            UUID.fromString("8a82a562-d83e-11f0-932a-56e62001cb1d"),
            "valentin",
            null,
            null,
            null,
            null,
            null,
            null
        )
    );
  }

  @Test
  void createCategoryTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    CategoryJson category = categoryDbClient.create(
        new CategoryJson(
            null,
            "покупка",
            "username",
            false
        )
    );
    System.out.println(category);
  }

  @Test
  void findAllCategoryTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    System.out.println(categoryDbClient.findAll());
  }

  @Test
  void findAllSpendTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    System.out.println(spendDbClient.findAllWithSpringJdbc());
  }
}
