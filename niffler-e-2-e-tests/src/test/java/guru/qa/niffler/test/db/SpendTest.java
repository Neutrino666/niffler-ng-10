package guru.qa.niffler.test.db;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.spend.SpendClient;
import guru.qa.niffler.service.spend.SpendDbClient;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class SpendTest {

  private static final SpendClient SPEND_DB_CLIENT = new SpendDbClient();

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
  void findAllByUsername() {
    System.out.println(SPEND_DB_CLIENT.findAllByUsername("admin"));
  }
}
