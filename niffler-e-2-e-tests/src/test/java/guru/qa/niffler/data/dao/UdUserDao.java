package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface UdUserDao {

  @Nonnull
  UserEntity create(@Nonnull UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(@Nonnull UUID id);
}
