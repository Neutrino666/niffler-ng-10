package guru.qa.niffler.service.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ParametersAreNonnullByDefault
public final class UserDbClient implements UserClient {

  private final static Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = AuthUserRepository.getInstance();
  private final UserdataUserRepository udUserRepository = UserdataUserRepository.getInstance();

  private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.userdataJdbcUrl()
  );

  @Nonnull
  @Override
  @Step("SQL Создание пользователя")
  public UserJson create(String username, String password) {
    return Objects.requireNonNull(xaTxTemplate.execute(() -> {
      AuthUserEntity authUser = authUserEntity(username, password);
      authUserRepository.create(authUser);
      return UserJson.fromEntity(udUserRepository.create(userEntity(username)));
    }));
  }

  @Nonnull
  @Step("SQL Поиск пользователя по id")
  public Optional<UserJson> findById(UUID id) {
    Optional<UserEntity> user = jdbcTxTemplate.execute(() -> udUserRepository.findById(id));
    return user != null && user.isPresent()
        ? user.map(UserJson::fromEntity)
        : Optional.empty();
  }

  @Nonnull
  @Override
  @Step("SQL Поиск пользователя по username")
  public Optional<UserJson> findByUsername(String username) {
    Optional<UserEntity> user = jdbcTxTemplate.execute(
        () -> udUserRepository.findByUsername(username));
    return user != null && user.isPresent()
        ? user.map(UserJson::fromEntity)
        : Optional.empty();
  }

  @Nonnull
  @Step("SQL Обновления данных пользователя")
  public UserJson update(UserJson user) {
    return Objects.requireNonNull(xaTxTemplate.execute(
        () -> UserJson.fromEntity(
            udUserRepository.update(UserEntity.fromJson(user)
            )
        )
    ));
  }

  @Nonnull
  @Override
  @Step("SQL Создание входящей заявки добавления в друзья")
  public List<UserJson> createIncomeInvitation(UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> xaTxTemplate.execute(() -> {
                String username = RandomDataUtils.getRandomName();
                AuthUserEntity authUser = authUserEntity(username, UserExtension.DEFAULT_PASSWORD);
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

  @Nonnull
  @Override
  @Step("SQL Создание исходящей заявки добавления в друзья")
  public List<UserJson> createOutcomeInvitation(UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> xaTxTemplate.execute(() -> {
                String username = RandomDataUtils.getRandomName();
                AuthUserEntity authUser = authUserEntity(username, UserExtension.DEFAULT_PASSWORD);
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

  @Nonnull
  @Step("SQL Создание друзей")
  public List<UserJson> createFriends(UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = udUserRepository.findById(targetUser.id())
          .orElseThrow();
      IntStream.range(0, count)
          .forEach(i -> xaTxTemplate.execute(() -> {
                String username = RandomDataUtils.getRandomName();
                AuthUserEntity authUser = authUserEntity(username, UserExtension.DEFAULT_PASSWORD);
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
  @Step("SQL Удаление пользователя")
  public void delete(UserJson user) {
    xaTxTemplate.execute(() -> {
      authUserRepository.findByUsername(user.username())
          .ifPresent(authUserRepository::remove);
      udUserRepository.findByUsername(user.username())
          .ifPresent(udUserRepository::remove);
      return null;
    });
  }

  private @Nonnull UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  @Nonnull
  private AuthUserEntity authUserEntity(String username, String password) {
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
