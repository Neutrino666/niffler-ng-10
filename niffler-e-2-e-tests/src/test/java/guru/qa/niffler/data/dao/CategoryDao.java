package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface CategoryDao {

  @Nonnull
  CategoryEntity create(@Nonnull CategoryEntity category);

  @Nonnull
  Optional<CategoryEntity> findById(@Nonnull UUID id);

  @Nonnull
  Optional<CategoryEntity> findByUsernameAndName(
      @Nonnull String username,
      @Nonnull String categoryName);

  @Nonnull
  List<CategoryEntity> findAllByUsername(@Nonnull String username);

  void delete(@Nonnull CategoryEntity category);
}
