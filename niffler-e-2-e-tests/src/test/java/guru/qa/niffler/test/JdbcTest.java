package guru.qa.niffler.test;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.category.CategoryDbClient;
import guru.qa.niffler.service.spend.SpendDbClient;
import guru.qa.niffler.service.user.UserDbClient;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class JdbcTest {

  @Test
  void createUserTest() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson user = userDbClient.create(
        new UserJson(
            null,
            "valentin",
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

  @Test
  void findByUsername() {
    UserDbClient user = new UserDbClient();
    System.out.println(user.findByUsername("valentin"));
  }

  @Test
  void findUserById() {
    UserDbClient user = new UserDbClient();
    System.out.println(user.findById(UUID.fromString("1fbc301c-a629-49ad-9e9f-d7aad0a8c828")));
  }

  @Test
  void deleteUser() {
    UserDbClient user = new UserDbClient();
    user.delete(
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
