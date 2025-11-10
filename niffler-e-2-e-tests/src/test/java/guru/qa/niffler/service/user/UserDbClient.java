package guru.qa.niffler.service.user;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.UserJson;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDbClient implements UserClient {

  private final static Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public UserJson createUserSpringJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("admin"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
        .create(authUser);

    AuthAuthorityEntity[] authorityEntities = Stream.of(Authority.values()).map(
        e -> {
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setUser(createdAuthUser);
          ae.setAuthority(e);
          return ae;
        }
    ).toArray(AuthAuthorityEntity[]::new);

    new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
        .create(authorityEntities);

    UserEntity ue = new UdUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
        .create(
            UserEntity.fromJson(user)
        );

    return UserJson.fromEntity(ue);
  }

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
