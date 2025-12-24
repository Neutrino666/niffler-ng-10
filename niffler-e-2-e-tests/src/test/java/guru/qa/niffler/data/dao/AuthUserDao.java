package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface AuthUserDao {

  @Nonnull
  AuthUserEntity create(@Nonnull AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(@Nonnull UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(@Nonnull String username);

  @Nonnull
  AuthUserEntity update(@Nonnull AuthUserEntity user);

  void delete(@Nonnull AuthUserEntity user);

}
