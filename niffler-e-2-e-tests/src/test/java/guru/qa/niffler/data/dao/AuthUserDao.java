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

  void delete(@Nonnull AuthUserEntity user);
}
