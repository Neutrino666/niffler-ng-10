package guru.qa.niffler.service.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UdUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
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

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
  private final UserdataUserRepository udUserRepository = new UdUserRepositoryJdbc();

  private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.userdataJdbcUrl()
  );

  @Nonnull
  @Override
  public UserJson create(@Nonnull UserJson user) {
    return xaTxTemplate.execute(() -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("admin"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      authUser.setAuthorities(Stream.of(Authority.values()).map(
          e -> {
            AuthAuthorityEntity ae = new AuthAuthorityEntity();
            ae.setUser(authUser);
            ae.setAuthority(e);
            return ae;
          }
      ).toList());

      authUserRepository.create(authUser);
      return UserJson.fromEntity(udUserRepository.create(UserEntity.fromJson(user)));
    });
  }

  @Nonnull
  @Override
  public Optional<UserJson> findById(@Nonnull UUID id) {
    Optional<UserEntity> user = jdbcTxTemplate.execute(() -> udUserRepository.findById(id));
    return user.map(UserJson::fromEntity);
  }

  @Nonnull
  @Override
  public Optional<UserJson> findByUsername(@Nonnull String username) {
    Optional<UserEntity> user = jdbcTxTemplate.execute(
        () -> udUserRepository.findByUsername(username));
    return user.map(UserJson::fromEntity);
  }

  @Override
  public void delete(@Nonnull UserJson user) {
    xaTxTemplate.execute(() -> {
      authUserRepository.findByUsername(user.username())
          .ifPresent(authUserRepository::delete);
      udUserRepository.delete(UserEntity.fromJson(user));
      return null;
    });
  }
}
