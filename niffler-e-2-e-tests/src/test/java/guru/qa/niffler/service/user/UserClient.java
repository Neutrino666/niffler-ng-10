package guru.qa.niffler.service.user;

import guru.qa.niffler.model.UserJson;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface UserClient {

  @Nonnull
  UserJson create(@Nonnull String username, @Nonnull String password);

  @Nonnull
  Optional<UserJson> findById(@Nonnull UUID id);

  @Nonnull
  Optional<UserJson> findByUsername(@Nonnull String username);

  @Nonnull
  UserJson update(@Nonnull UserJson user);

  void createIncomeInvitation(@Nonnull UserJson targetUser, int count);

  void createOutcomeInvitation(@Nonnull UserJson targetUser, int count);

  void delete(@Nonnull UserJson user);
}
