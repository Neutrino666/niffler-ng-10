package guru.qa.niffler.service.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDbClient implements UserClient {

  private final static Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
  private final UserdataUserRepository udUserRepository = new UserdataUserRepositoryHibernate();

  private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.userdataJdbcUrl()
  );

  @Nonnull
  @Override
  public UserJson create(@Nonnull String username, @Nonnull String password) {
    return xaTxTemplate.execute(() -> {
      AuthUserEntity authUser = authUserEntity(username, password);
      authUserRepository.create(authUser);
      return UserJson.fromEntity(udUserRepository.create(userEntity(username)));
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

  public void addIncomeInvitation(@Nonnull UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> {
                xaTxTemplate.execute(() -> {
                  String username = RandomDataUtils.getRandomName();
                  AuthUserEntity authUser = authUserEntity(username, "12345");
                  authUserRepository.create(authUser);
                  UserEntity addressee = udUserRepository.create(userEntity(username));
                  udUserRepository.addIncomeInvitation(targetEntity, addressee);
                  return null;
                });
              }
          );
    }
  }

  public void addOutcomeInvitation(@Nonnull UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> {
                xaTxTemplate.execute(() -> {
                  String username = RandomDataUtils.getRandomName();
                  AuthUserEntity authUser = authUserEntity(username, "12345");
                  authUserRepository.create(authUser);
                  UserEntity addressee = udUserRepository.create(userEntity(username));
                  udUserRepository.addOutcomeInvitation(targetEntity, addressee);
                  return null;
                });
              }
          );
    }
  }

  public void addFriend(@Nonnull UserJson targetUser, int count) {
    throw new RuntimeException("Not implemented :(");
//    jdbcTxTemplate.execute(
//        () -> {
//          udUserRepository.addFriend(UserEntity.fromJson(requester),
//              UserEntity.fromJson(addressee));
//          return null;
//        }
//    );
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

  private @Nonnull UserEntity userEntity(@Nonnull String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  @Nonnull
  private AuthUserEntity authUserEntity(@Nonnull String username, @Nonnull String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
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
    return authUser;
  }
}
