package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

  private final static Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.userdataJdbcUrl());

  @Nonnull
  @Override
  public UserEntity create(@Nonnull UserEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findById(@Nonnull UUID id) {
    return Optional.ofNullable(entityManager.find(UserEntity.class, id));
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findByUsername(@Nonnull String username) {
    try {
      return Optional.of(entityManager.createQuery(
              "SELECT u FROM UserEntity u  WHERE u.username =: username",
              UserEntity.class)
          .setParameter("username", username)
          .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public void addIncomeInvitation(@Nonnull UserEntity requester, UserEntity addressee) {
    entityManager.joinTransaction();
    addressee.addFriends(FriendshipStatus.PENDING, requester);
  }

  @Override
  public void addOutcomeInvitation(@Nonnull UserEntity requester, UserEntity addressee) {
    entityManager.joinTransaction();
    requester.addFriends(FriendshipStatus.PENDING, addressee);
  }

  @Override
  public void addFriend(@Nonnull UserEntity requester, UserEntity addressee) {
    entityManager.joinTransaction();
    requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
    addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
  }

  @Override
  public void delete(@Nonnull UserEntity user) {

  }
}
