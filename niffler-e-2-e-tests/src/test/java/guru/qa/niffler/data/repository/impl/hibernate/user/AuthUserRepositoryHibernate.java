package guru.qa.niffler.data.repository.impl.hibernate.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class AuthUserRepositoryHibernate implements AuthUserRepository {

  private final static Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.authJdbcUrl());

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  @Nonnull
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    entityManager.joinTransaction();
    return entityManager.merge(user);
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try {
      return Optional.of(entityManager.createQuery(
              "SELECT u FROM AuthUserEntity u WHERE u.username = :username",
              AuthUserEntity.class)
          .setParameter("username", username)
          .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(AuthUserEntity user) {
    entityManager.joinTransaction();
    if (!entityManager.contains(user)) {
      entityManager.merge(user);
    }
    entityManager.remove(user);
  }
}
