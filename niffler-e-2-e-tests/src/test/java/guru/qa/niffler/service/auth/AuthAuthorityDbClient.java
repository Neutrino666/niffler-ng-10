package guru.qa.niffler.service.auth;

import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public class AuthAuthorityDbClient {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  public AuthAuthorityEntity create(@Nonnull AuthAuthorityEntity authUserEntity) {
    return transaction(connection -> {
          return new AuthAuthorityDaoJdbc(connection).create(authUserEntity);
        },
        CFG.authJdbcUrl()
    );
  }

  @Nonnull
  public List<AuthAuthorityEntity> findAllByUserId(@Nonnull UUID id) {
    return transaction(connection -> {
          return new AuthAuthorityDaoJdbc(connection).findAllByUserId(id);
        },
        CFG.authJdbcUrl()
    );
  }

  public void delete(@Nonnull AuthAuthorityEntity authAuthorityEntity) {
    transaction(connection -> {
          new AuthAuthorityDaoJdbc(connection).delete(authAuthorityEntity);
        },
        CFG.authJdbcUrl()
    );
  }
}
