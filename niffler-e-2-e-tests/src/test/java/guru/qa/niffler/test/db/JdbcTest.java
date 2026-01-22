package guru.qa.niffler.test.db;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.extension.SpendClientInjector;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.spend.SpendClient;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpendClientInjector.class)
public class JdbcTest {

  private SpendClient spendClient;

  @Test
  void createTest() {
    System.out.println(spendClient.create(new SpendJson(
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
}
