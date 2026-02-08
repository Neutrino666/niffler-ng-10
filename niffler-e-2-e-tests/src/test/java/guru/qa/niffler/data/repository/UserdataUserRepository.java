package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.impl.hibernate.user.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.user.UdUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.user.UdUserRepositorySpringJdbc;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {

  @Nonnull
  static UserdataUserRepository getInstance() {
    return switch (System.getProperty("repository", "jpa")) {
      case "jpa" -> new UserdataUserRepositoryHibernate();
      case "jdbc" -> new UdUserRepositoryJdbc();
      case "spring-jdbc" -> new UdUserRepositorySpringJdbc();
      default -> throw new IllegalArgumentException("Неизвестный тип репозитория: "
          + System.getProperty("repository"));
    };
  }

  @Nonnull
  UserEntity create(UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  @Nonnull
  UserEntity update(UserEntity user);

  void sendInvitation(UserEntity requester, UserEntity addressee);

  void addFriend(UserEntity requester, UserEntity addressee);

  void remove(UserEntity user);
}
