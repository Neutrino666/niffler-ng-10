package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.CategoryJson;
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
            "hibernateTest",
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

  @Test
  void findAllAuthority() {
    UserDbClient user = new UserDbClient();
    System.out.println(user.findAllAuthority(UUID.fromString("c9b8b756-f3cc-4186-853b-590bb1418c85")));
  }

  @Test
  void findByUsername() {
    UserDbClient user = new UserDbClient();
    System.out.println(user.findByUsername("admin"));
  }
}
