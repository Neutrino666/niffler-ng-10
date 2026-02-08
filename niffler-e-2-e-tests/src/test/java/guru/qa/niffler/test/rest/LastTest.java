package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.service.user.UserClient;
import guru.qa.niffler.service.user.UsersApiClient;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

@Isolated
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("REST Users")
@ParametersAreNonnullByDefault
public final class LastTest {

  private UserClient usersApiClient = new UsersApiClient();

  @User
  @Test
  @DisplayName("Список пользователей не пуст")
  void getAllUsersIsNotEmpty() {
    int usersCount = usersApiClient.getAllUsers("", "").size();
    int minUsersCount = 1;
    Assertions.assertThat(usersCount)
        .describedAs("Пользователей в системе: %d ожидается больше или равно: %d",
            usersCount, minUsersCount)
        .isGreaterThanOrEqualTo(minUsersCount);
  }
}
