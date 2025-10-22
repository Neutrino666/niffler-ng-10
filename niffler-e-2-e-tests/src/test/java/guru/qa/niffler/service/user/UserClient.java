package guru.qa.niffler.service.user;

import guru.qa.niffler.data.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface UserClient {

  @Nonnull
  UserEntity create(@Nonnull UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(@Nonnull UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(@Nonnull String username);

  void delete(@Nonnull UserEntity user);
}
