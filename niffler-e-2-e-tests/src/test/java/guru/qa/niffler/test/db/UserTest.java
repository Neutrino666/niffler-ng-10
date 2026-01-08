package guru.qa.niffler.test.db;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.UserDbClient;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UserTest {

  private static final UserDbClient USER_DB_CLIENT = new UserDbClient();
  private final String ERROR_MSG = "Not found by username - ";

  @ValueSource(strings = {
      "valentin6"
  })
  @ParameterizedTest
  void createUserTest(String username) {
    UserJson user = USER_DB_CLIENT.create(
        username, "12345"
    );
    System.out.println(user);
  }

  @Test
  void findUserByIdTest() {
    System.out.println(
        USER_DB_CLIENT.findById(UUID.fromString("e30a45b6-e052-11f0-92e3-36c6088cf3f6")));
  }

  @Test
  void findByUsernameTest() {
    System.out.println(USER_DB_CLIENT.findByUsername("valentin6"));
  }

  @Test
  void sendInvitationTest() {
    USER_DB_CLIENT.createIncomeInvitation(
        new UserJson(
            UUID.fromString("e30a45b6-e052-11f0-92e3-36c6088cf3f6"),
            "valentin6",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ) ,
        1
    );
  }

  @Test
  void createFriendTest() {
    USER_DB_CLIENT.createFriends(
        new UserJson(
            UUID.fromString("e30a45b6-e052-11f0-92e3-36c6088cf3f6"),
            "valentin6",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ) ,
        1
    );
  }

  @Test
  void updateTest() {
    UserJson user = USER_DB_CLIENT.findByUsername("admin")
        .orElseThrow(() -> new RuntimeException(ERROR_MSG + "admin"));
    System.out.println(USER_DB_CLIENT.update(
            new UserJson(
                user.id(),
                user.username(),
                user.firstname(),
                RandomDataUtils.getRandomSurname(),
                user.fullname(),
                user.currency(),
                user.photo(),
                user.photoSmall(),
                user.friendshipStatus(),
                user.testData()
            )
        )
    );
  }

  @Test
  void deleteUserTest() {
    USER_DB_CLIENT.delete(
        new UserJson(
            null,
            "valentin6",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    );
  }
}
