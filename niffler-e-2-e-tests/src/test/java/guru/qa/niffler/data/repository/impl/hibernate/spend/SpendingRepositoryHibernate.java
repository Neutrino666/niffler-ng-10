package guru.qa.niffler.data.repository.impl.hibernate.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpendingRepositoryHibernate implements SpendRepository {

  private final static Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.spendJdbcUrl());

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    entityManager.joinTransaction();
    entityManager.persist(spend);
    return spend;
  }

  @Nonnull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    entityManager.joinTransaction();
    entityManager.persist(category);
    return category;
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username,
      String name) {
    CategoryEntity category = entityManager.createQuery(
            "SELECT c FROM CategoryEntity c "
                + "WHERE c.username = ?1 "
                + "AND c.name = ?2",
            CategoryEntity.class
        )
        .setParameter(1, username)
        .setParameter(2, name)
        .getSingleResult();
    return Optional.ofNullable(category);
  }

  @Nonnull
  @Override
  public SpendEntity update(SpendEntity spend) {
    entityManager.joinTransaction();
    return entityManager.merge(spend);
  }

  @Nonnull
  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    entityManager.joinTransaction();
    return entityManager.merge(category);
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    return entityManager.createQuery(
            "FROM SpendEntity s WHERE s.username = :username",
            SpendEntity.class)
        .setParameter("username", username)
        .getResultList();
  }

  @Override
  public @Nonnull List<CategoryEntity> findAllCategoryByUsername(String username) {
    return entityManager.createQuery(
            "FROM CategoryEntity s WHERE s.username = :username",
            CategoryEntity.class)
        .setParameter("username", username)
        .getResultList();
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username,
      String description) {
    SpendEntity spend = entityManager.createQuery(
            "FROM SpendEntity s "
                + "WHERE s.username = ?1 "
                + "AND s.description = ?2",
            SpendEntity.class
        )
        .setParameter(1, username)
        .setParameter(2, description)
        .getSingleResult();
    return Optional.ofNullable(spend);
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    return entityManager.createQuery(
            "FROM SpendEntity s",
            SpendEntity.class)
        .getResultList();
  }

  @Override
  public void remove(SpendEntity spend) {
    entityManager.joinTransaction();
    if (!entityManager.contains(spend)) {
      spend = entityManager.merge(spend);
    }
    entityManager.remove(spend);
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    CategoryEntity ce = findCategoryById(category.getId())
        .orElseThrow(
            () -> new RuntimeException("Not found category by id")
        );
    entityManager.joinTransaction();
    if (!entityManager.contains(ce)) {
      entityManager.merge(ce);
    }
    entityManager.remove(ce);
  }
}
