package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface UserDao {

  @Nonnull
  UserEntity create(@Nonnull UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(@Nonnull UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(@Nonnull String username);

  void delete(@Nonnull UserEntity user);
}
