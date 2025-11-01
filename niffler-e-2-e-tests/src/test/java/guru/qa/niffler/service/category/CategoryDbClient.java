package guru.qa.niffler.service.category;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class CategoryDbClient implements CategoryClient {

  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public @Nonnull Optional<CategoryEntity> findByUsernameAndName(
      @Nonnull String username,
      @Nonnull String categoryName
  ) {
    return categoryDao.findByUsernameAndName(username, categoryName);
  }

  public @Nonnull List<CategoryEntity> findAllByUsername(@Nonnull String username) {
    return categoryDao.findAllByUsername(username);
  }

  public void delete(@Nonnull CategoryEntity category) {
    categoryDao.delete(category);
  }

  @Override
  public @Nonnull CategoryJson update(@Nonnull CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented :(");
  }

  @Override
  public @Nonnull CategoryJson create(@Nonnull CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
  }
}
