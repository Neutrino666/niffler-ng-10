package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.meta.RestTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import guru.qa.niffler.service.gateway.GatewayV2ApiClient;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
@DisplayName("REST Friends V2")
public final class FriendsV2Test {

  @RegisterExtension
  private static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
  private final GatewayV2ApiClient gatewayV2ApiClient = new GatewayV2ApiClient();

  @User(
      incomeInvitations = 1,
      friends = 2
  )
  @ApiLogin
  @Test
  @DisplayName("GET api/v2/friends/all")
  void allFriendsAndIncomeInvitationsShouldBeReturned(@Token String token) {
    final RestResponsePage<UserJson> result =
        gatewayV2ApiClient.allFriends(token, 0, 10, List.of("username,asc"), null);
    Assertions.assertThat(result.getContent().size())
        .isEqualTo(3);
  }
}
