package guru.qa.niffler.test.db;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.spend.SpendDbClient;
import java.util.Date;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Test;

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
  void updateTest() {
    String id = "c8e42ffc-4ebc-46c4-9762-41f9e522329e";
    SpendJson spend = SPEND_DB_CLIENT.findById(UUID.fromString(id))
        .orElseThrow(() -> new RuntimeException("Not found spend by id: " + id));
    System.out.println(SPEND_DB_CLIENT.update(new SpendJson(
        spend.id(),
        spend.spendDate(),
        spend.category(),
        CurrencyValues.EUR,
        222.,
        spend.description(),
        spend.username()
    )));
  }

  @Test
  void updateCategoryTest() {
    String id = "c69179d6-ae84-11f0-b2b4-1a4f9b3b16be";
    CategoryJson category = SPEND_DB_CLIENT.findCategoryById(UUID.fromString(id))
        .orElseThrow(() -> new RuntimeException("Not found spend by id: " + id));
    System.out.println(SPEND_DB_CLIENT.updateCategory(
            new CategoryJson(
                category.id(),
                RandomDataUtils.getRandomName(),
                category.username(),
                category.archived()
            )
        )
    );
  }

  @Test
  void findByIdTest() {
    System.out.println(
        SPEND_DB_CLIENT.findById(UUID.fromString("c8e42ffc-4ebc-46c4-9762-41f9e522329e"))
    );
  }

  @Test
  void findCategoryByIdTest() {
    System.out.println(
        SPEND_DB_CLIENT.findCategoryById(UUID.fromString("71d851b6-75be-4cfb-a0fd-db1006f92383"))
    );
  }

  @Test
  void findCategoryByUsernameAndSpendNameTest() {
    System.out.println(
        SPEND_DB_CLIENT.findCategoryByUsernameAndSpendName("admin", "Geoffrey Marks")
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

  @Test
  void findAllByUsername() {
    System.out.println(SPEND_DB_CLIENT.findAllByUsername("admin"));
  }

  @Test
  void removeTest() {
    String id = "6840503e-3de3-4fa4-a880-c6c351b54b9a";
    SPEND_DB_CLIENT.remove(
        SPEND_DB_CLIENT.findById(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("Not found spend by id: " + id))
    );
  }

  @Test
  void removeCategoryTest() {
    SPEND_DB_CLIENT.removeCategory(
        new CategoryJson(
            UUID.fromString("459e90d0-b798-424c-bad9-014a04131166"),
            null,
            null,
            false
        )
    );
  }
}
