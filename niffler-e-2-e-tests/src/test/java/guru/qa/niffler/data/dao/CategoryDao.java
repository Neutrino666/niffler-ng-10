package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.category.CategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface CategoryDao {

  CategoryEntity create(CategoryEntity category);

  Optional<CategoryEntity> findById(UUID id);

  Optional<CategoryEntity> findByUsernameAndName(
      @Nonnull String username,
      @Nonnull String categoryName);

  List<CategoryEntity> findAllByUsername(String username);

  void delete(CategoryEntity category);
}
