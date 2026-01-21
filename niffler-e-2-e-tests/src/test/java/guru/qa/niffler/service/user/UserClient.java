package guru.qa.niffler.service.user;

import guru.qa.niffler.model.UserJson;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserClient {

  @Nonnull
  UserJson create(String username, String password);

  @Nonnull
  Optional<UserJson> findByUsername(String username);

  @Nonnull
  List<UserJson> createIncomeInvitation(UserJson targetUser, int count);

  @Nonnull
  List<UserJson> createOutcomeInvitation(UserJson targetUser, int count);

  @Nonnull
  List<UserJson> createFriends(UserJson targetUser, int count);

  void delete(UserJson user);
}
