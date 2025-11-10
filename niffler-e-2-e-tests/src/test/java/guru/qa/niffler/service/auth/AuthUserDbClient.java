package guru.qa.niffler.service.auth;

import static guru.qa.niffler.data.Databases.xaTransaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaConsumer;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class AuthUserDbClient {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  public AuthUserEntity create(@Nonnull AuthUserEntity user) {
    user.setPassword(encodePassword(
        user.getPassword()
    ));
    AuthUserEntity result = new AuthUserEntity();
    xaTransaction(
        new XaConsumer(connection -> {
          new AuthUserDaoJdbc(connection).create(user);
        },
            CFG.authJdbcUrl()
        ),
        new XaConsumer(connection -> {
          AuthAuthorityDaoJdbc authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc(connection);
          user.getAuthorities().forEach(authority -> {
            AuthAuthorityEntity authAuthorityEntity = new AuthAuthorityEntity();
            authAuthorityEntity.setUser(user);
            authAuthorityEntity.setAuthority(authority.getAuthority());
            authAuthorityDaoJdbc.create(authAuthorityEntity);
          });
        },
            CFG.authJdbcUrl()
        )
    );
    return result;
  }

  @Nonnull
  public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
    AuthUserEntity user = new AuthUserEntity();
    List<AuthAuthorityEntity> authorities = xaTransaction(
        new XaFunction<>(connection -> new AuthAuthorityDaoJdbc(connection)
            .findAllByUserId(id),
            CFG.authJdbcUrl()
        )
    );
    if (!authorities.isEmpty()) {
      user.copy(authorities.getFirst().getUser());
      authorities.forEach(a -> a.setUser(null));
      user.setAuthorities(authorities);
    }
    return authorities.isEmpty() ? Optional.empty() : Optional.of(user);
  }

  public void delete(@Nonnull AuthUserEntity user) {
    xaTransaction(
        new XaConsumer(connection -> user
            .getAuthorities()
            .forEach(authority ->
                new AuthAuthorityDaoJdbc(connection).delete(authority)),
            CFG.authJdbcUrl()
        ),
        new XaConsumer(
            connection -> new AuthUserDaoJdbc(connection).delete(user),
            CFG.authJdbcUrl()
        )
    );
  }

  @Nonnull
  private String encodePassword(@Nonnull String password) {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder()
        .encode(password);
  }
}
