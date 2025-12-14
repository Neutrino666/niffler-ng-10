package guru.qa.niffler.service.user;

import guru.qa.niffler.model.UserJson;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface UserClient {

  @Nonnull
  UserJson create(@Nonnull String username, @Nonnull  String password);

  @Nonnull
  Optional<UserJson> findById(@Nonnull UUID id);

  @Nonnull
  Optional<UserJson> findByUsername(@Nonnull String username);

  void delete(@Nonnull UserJson user);
}
