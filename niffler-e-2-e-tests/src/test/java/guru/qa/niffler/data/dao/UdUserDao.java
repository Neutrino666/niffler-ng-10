package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UdUserDao {

  @Nonnull
  UserEntity create(UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  void delete(UserEntity user);
}
