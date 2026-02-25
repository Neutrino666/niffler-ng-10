package guru.qa.niffler.test.db;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.spend.SpendDbClient;
import java.util.Date;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@ParametersAreNonnullByDefault
public class SpendTest {

  private static final SpendDbClient SPEND_DB_CLIENT = new SpendDbClient();

  @Test
  void createTest() {
    System.out.println(SPEND_DB_CLIENT.create(new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                RandomDataUtils.getRandomName(),
                "admin",
                false
            ),
            CurrencyValues.RUB,
            1.1,
            this.getClass().getSimpleName(),
            "admin"
        ))
    );
  }

  @Test
  void findCategoryByIdTest() {
    System.out.println(
        SPEND_DB_CLIENT.findCategoryById(UUID.fromString("71d851b6-75be-4cfb-a0fd-db1006f92383"))
    );
  }

  @Test
  void findByUsernameAndSpendDescriptionTest() {
    System.out.println(
        SPEND_DB_CLIENT.findByUsernameAndSpendDescription(
            "admin",
            this.getClass().getSimpleName())
    );
  }

  @Test
  void findAll() {
    System.out.println(SPEND_DB_CLIENT.findAll());
  }
}
