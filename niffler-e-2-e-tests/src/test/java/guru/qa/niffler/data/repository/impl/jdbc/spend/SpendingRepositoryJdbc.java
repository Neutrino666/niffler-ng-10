package guru.qa.niffler.data.repository.impl.jdbc.spend;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpendingRepositoryJdbc implements SpendRepository {

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  @Nonnull
  @Override
  public SpendEntity create(@Nonnull SpendEntity spend) {
    if (spend.getCategory() != null) {
      spend.setCategory(createCategory(spend.getCategory()));
    }
    return spendDao.create(spend);
  }

  @Nonnull
  @Override
  public CategoryEntity createCategory(@Nonnull CategoryEntity category) {
    return categoryDao.create(category);
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
    return categoryDao.findById(id);
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(
      @Nonnull String username,
      @Nonnull String name) {
    return categoryDao.findByUsernameAndName(username, name);
  }

  @Nonnull
  @Override
  public SpendEntity update(@Nonnull SpendEntity spend) {
    return spendDao.update(spend);
  }

  @Nonnull
  @Override
  public CategoryEntity updateCategory(@Nonnull CategoryEntity category) {
    return categoryDao.update(category);
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(@Nonnull UUID id) {
    return spendDao.findById(id);
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAllByUsername(@Nonnull String username) {
    return spendDao.findAllByUsername(username);
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(
      @Nonnull String username,
      @Nonnull String description) {
    return spendDao.findByUsernameAndSpendDescription(username, description);
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    return spendDao.findAll();
  }

  @Override
  public void remove(@Nonnull SpendEntity spend) {
    spendDao.remove(spend);
  }

  @Override
  public void removeCategory(@Nonnull CategoryEntity category) {
    categoryDao.remove(category);
  }
}
