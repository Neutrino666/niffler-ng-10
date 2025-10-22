package guru.qa.niffler.service.category;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class CategoryDbClient implements CategoryClient {

  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public Optional<CategoryEntity> findByUsernameAndName(
      @Nonnull String username,
      @Nonnull String categoryName
  ) {
    return categoryDao.findByUsernameAndName(username, categoryName);
  }

  public List<CategoryEntity> findAllByUsername(@Nonnull String username) {
    return categoryDao.findAllByUsername(username);
  }

  public void delete(CategoryEntity category) {
    categoryDao.delete(category);
  }

  @Override
  public CategoryJson update(CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented :(");
  }

  @Override
  public CategoryJson create(CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
  }
}
