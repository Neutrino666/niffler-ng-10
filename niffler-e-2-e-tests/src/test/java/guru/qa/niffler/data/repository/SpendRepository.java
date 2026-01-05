package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface SpendRepository {

  @Nonnull
  SpendEntity create(@Nonnull SpendEntity spend);

  @Nonnull
  CategoryEntity createCategory(@Nonnull CategoryEntity category);

  @Nonnull
  Optional<CategoryEntity> findCategoryById(@Nonnull final UUID id);

  @Nonnull
  Optional<CategoryEntity> findCategoryByUsernameAndSpendName(
      @Nonnull String username,
      @Nonnull String name
  );

  @Nonnull
  SpendEntity update(@Nonnull SpendEntity spend);

  @Nonnull
  CategoryEntity updateCategory(@Nonnull CategoryEntity category);

  @Nonnull
  Optional<SpendEntity> findById(@Nonnull UUID id);

  @Nonnull
  List<SpendEntity> findAllByUsername(@Nonnull String username);

  @Nonnull
  Optional<SpendEntity> findByUsernameAndSpendDescription(
      @Nonnull String username,
      @Nonnull String description
  );

  @Nonnull
  List<SpendEntity> findAll();

  void remove(@Nonnull SpendEntity spend);

  void removeCategory(@Nonnull CategoryEntity category);
}
