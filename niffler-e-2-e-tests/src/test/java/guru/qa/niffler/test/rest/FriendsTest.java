package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.gateway.GatewayApiClient;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("REST Friends")
public final class FriendsTest extends BaseRestTest {

  private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

  @User(
      incomeInvitations = 1,
      friends = 2
  )
  @ApiLogin
  @Test
  @DisplayName("GET api/friends/all")
  void allFriendsAndIncomeInvitationsShouldBeReturned(@Token String token) {
    final List<UserJson> result = gatewayApiClient.allFriends(token, null);
    Assertions.assertThat(result)
        .hasSize(3);
  }
}
