package guru.qa.niffler.data.repository.impl.spring.spend;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategorySpringDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendingSpringDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpendingRepositorySpring implements SpendRepository {

  private final SpendDao spendDao = new SpendingSpringDaoJdbc();
  private final CategoryDao categoryDao = new CategorySpringDaoJdbc();

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    if (spend.getCategory().getId() == null) {
      spend.getCategory().setId(
          createCategory(spend.getCategory()).getId()
      );
    }
    return spendDao.create(spend);
  }

  @Nonnull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.create(category);
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return categoryDao.findById(id);
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username,
      String name) {
    return categoryDao.findByUsernameAndName(username, name);
  }

  @Nonnull
  @Override
  public SpendEntity update(SpendEntity spend) {
    return spendDao.update(spend);
  }

  @Nonnull
  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    return categoryDao.update(category);
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    return spendDao.findById(id);
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    return spendDao.findAllByUsername(username);
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username,
      String description) {
    return spendDao.findByUsernameAndSpendDescription(username, description);
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    return spendDao.findAll();
  }

  @Override
  public void remove(SpendEntity spend) {
    spendDao.remove(spend);
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    categoryDao.remove(category);
  }
}
