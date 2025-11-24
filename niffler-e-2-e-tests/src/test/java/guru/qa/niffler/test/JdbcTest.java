package guru.qa.niffler.test;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.category.CategoryDbClient;
import guru.qa.niffler.service.spend.SpendDbClient;
import guru.qa.niffler.service.user.UserDbClient;
import org.junit.jupiter.api.Test;

public class JdbcTest {

  @Test
  void createUserSpringJdbcTest() {
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

  @Test
  void createCategorySpringJdbcTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    CategoryJson category = categoryDbClient.createWithSpringJdbc(new CategoryJson(
        null,
        "Tommy",
        "Emmanuel",
        false
    ));
    System.out.println(category);
    categoryDbClient.deleteWithSpringJdbc(CategoryEntity.fromJson(category));
  }

  @Test
  void findAllCategorySpringJdbcTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    System.out.println(categoryDbClient.findAllWithSpringJdbc());
  }

  @Test
  void findAllCategoryTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    System.out.println(categoryDbClient.findAll());
  }

  @Test
  void findAllSpendWithSpringJdbcTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    System.out.println(spendDbClient.findAllWithSpringJdbc());
  }
}
