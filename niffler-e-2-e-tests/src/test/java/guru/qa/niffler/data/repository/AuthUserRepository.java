package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface AuthUserRepository {

  @Nonnull
  AuthUserEntity create(@Nonnull AuthUserEntity user);

  @Nonnull
  AuthUserEntity update(@Nonnull AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(@Nonnull UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(@Nonnull String username);

  void remove(@Nonnull AuthUserEntity user);
}