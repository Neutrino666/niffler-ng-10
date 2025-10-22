package guru.qa.niffler.service.user;

import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UserDbClient implements UserClient {

  private final static Config CFG = Config.getInstance();

  @Override
  public @Nonnull UserEntity create(@Nonnull UserEntity user) {
    return transaction(connection -> {
          return new UserDaoJdbc(connection).create(user);
        },
        CFG.userdataJdbcUrl()
    );
  }

  @Override
  public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
    return transaction(connection -> {
          return new UserDaoJdbc(connection).findById(id);
        },
        CFG.userdataJdbcUrl()
    );
  }

  @Override
  public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
    return transaction(connection -> {
          return new UserDaoJdbc(connection).findByUsername(username);
        },
        CFG.userdataJdbcUrl()
    );
  }

  @Override
  public void delete(@Nonnull UserEntity user) {
    transaction(connection -> {
          new UserDaoJdbc(connection).delete(user);
        },
        CFG.userdataJdbcUrl()
    );
  }
}
