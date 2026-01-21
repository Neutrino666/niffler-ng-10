package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthUserDao {

  @Nonnull
  AuthUserEntity create(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);

  @Nonnull
  AuthUserEntity update(AuthUserEntity user);

  void delete(AuthUserEntity user);

}
