package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.UserDbClient;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class FriendshipTest {

  @Test
  public void addIncomeTest() {
    UserDbClient userDbClient = new UserDbClient();
    userDbClient.addIncomeInvitation(
        new UserJson(
            UUID.fromString("6c326031-9486-4628-b68a-3b026b240206"),
            null,
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        ),
        new UserJson(
            UUID.fromString("b0c8e0d3-53f3-464f-b37b-3f5352a4d54a"),
            null,
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        )
    );
  }

  @Test
  public void addOutcomeTest() {
    UserDbClient userDbClient = new UserDbClient();
    userDbClient.addOutcomeInvitation(
        new UserJson(
            UUID.fromString("6c326031-9486-4628-b68a-3b026b240206"),
            null,
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        ),
        new UserJson(
            UUID.fromString("b0c8e0d3-53f3-464f-b37b-3f5352a4d54a"),
            null,
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        )
    );
  }

  @Test
  public void addFriendTest() {
    UserDbClient userDbClient = new UserDbClient();
    userDbClient.addFriend(
        new UserJson(
            UUID.fromString("6c326031-9486-4628-b68a-3b026b240206"),
            null,
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        ),
        new UserJson(
            UUID.fromString("b0c8e0d3-53f3-464f-b37b-3f5352a4d54a"),
            null,
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null
        )
    );
  }
}
