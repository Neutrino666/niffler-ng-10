package guru.qa.niffler.test.rest;

import guru.qa.niffler.service.user.UsersApiClient;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Order(1)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("REST Users")
@ParametersAreNonnullByDefault
public class FirstTest {

  private UsersApiClient usersApiClient = new UsersApiClient();

  @Test
  @DisplayName("Список пользователей пуст")
  void getAllUsersIsEmpty() {
    int usersCount = usersApiClient.getAllUsers("", "").size();
    int maxUser = 0;
    Assertions.assertThat(usersCount)
        .describedAs("Пользователей в системе: %d ожидается: %d",
            usersCount, maxUser)
        .isEqualTo(maxUser);
  }
}
