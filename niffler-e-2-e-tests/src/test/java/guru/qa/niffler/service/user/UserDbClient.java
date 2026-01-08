package guru.qa.niffler.service.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.user.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.user.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.user.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.user.UdUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.user.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.user.UdUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import java.util.ArrayList;
import java.util.List;
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

  @Nonnull
  @Override
  public UserJson update(@Nonnull UserJson user) {
    return xaTxTemplate.execute(
        () -> UserJson.fromEntity(
            udUserRepository.update(UserEntity.fromJson(user)
            )
        )
    );
  }

  @Override
  public List<UserJson> createIncomeInvitation(@Nonnull UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> xaTxTemplate.execute(() -> {
                String username = RandomDataUtils.getRandomName();
                AuthUserEntity authUser = authUserEntity(username, "12345");
                authUserRepository.create(authUser);
                UserEntity requester = udUserRepository.create(userEntity(username));
                udUserRepository.sendInvitation(requester, targetEntity);
                result.add(UserJson.fromEntity(requester));
                return null;
              })
          );
    }
    return result;
  }

  @Override
  public List<UserJson> createOutcomeInvitation(@Nonnull UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> xaTxTemplate.execute(() -> {
                String username = RandomDataUtils.getRandomName();
                AuthUserEntity authUser = authUserEntity(username, "12345");
                authUserRepository.create(authUser);
                UserEntity addressee = udUserRepository.create(userEntity(username));
                udUserRepository.sendInvitation(targetEntity, addressee);
                result.add(UserJson.fromEntity(addressee));
                return null;
              })
          );
    }
    return result;
  }

  public List<UserJson> createFriends(@Nonnull UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> xaTxTemplate.execute(() -> {
                String username = RandomDataUtils.getRandomName();
                AuthUserEntity authUser = authUserEntity(username, "12345");
                authUserRepository.create(authUser);
                UserEntity friend = udUserRepository.create(userEntity(username));
                udUserRepository.addFriend(friend, targetEntity);
                result.add(UserJson.fromEntity(friend));
                return null;
              })
          );
    }
    return result;
  }

  @Override
  public void delete(@Nonnull UserJson user) {
    xaTxTemplate.execute(() -> {
      authUserRepository.findByUsername(user.username())
          .ifPresent(authUserRepository::remove);
      udUserRepository.findByUsername(user.username())
          .ifPresent(udUserRepository::remove);
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
