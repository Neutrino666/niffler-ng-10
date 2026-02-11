package guru.qa.niffler.data.repository.impl.hibernate.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class SpendingRepositoryHibernate implements SpendRepository {

  private final static Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.spendJdbcUrl());

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    entityManager.joinTransaction();
    if (spend.getCategory().getId() != null && !entityManager.contains(spend.getCategory())) {
      CategoryEntity categoryRef = entityManager.getReference(CategoryEntity.class,
          spend.getCategory().getId());
      spend.setCategory(categoryRef);
    }
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

  @Override
  public @Nonnull List<CategoryEntity> findAllCategory() {
    return entityManager.createQuery(
            "FROM CategoryEntity s",
            CategoryEntity.class)
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

  @Nonnull
  public List<SpendEntity> all(String username, @Nullable CurrencyValues currency,
      @Nullable Date from, @Nullable Date to) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SpendEntity> cq = cb.createQuery(SpendEntity.class);
    final Root<SpendEntity> root = cq.from(SpendEntity.class);

    final List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.equal(root.get("username"), username));
    if (currency != null) {
      predicates.add(cb.equal(root.get("currency"), currency));
    }
    if (from != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get("spendDate"), from));
    }
    if (to != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get("spendDate"), to));
    }

    cq.select(root).where(predicates.toArray(new Predicate[0]))
        .orderBy(cb.desc(root.get("spendDate")));

    return entityManager.createQuery(cq).getResultList();
  }

  @Override
  public void remove(SpendEntity spend) {
    entityManager.joinTransaction();
    entityManager.remove(entityManager.contains(spend) ? spend : entityManager.merge(spend));
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
