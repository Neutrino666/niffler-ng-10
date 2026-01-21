package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SpendRepository {

  @Nonnull
  SpendEntity create(SpendEntity spend);

  @Nonnull
  CategoryEntity createCategory(CategoryEntity category);

  @Nonnull
  Optional<CategoryEntity> findCategoryById(final UUID id);

  @Nonnull
  Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name
  );

  @Nonnull
  SpendEntity update(SpendEntity spend);

  @Nonnull
  CategoryEntity updateCategory(CategoryEntity category);

  @Nonnull
  Optional<SpendEntity> findById(UUID id);

  @Nonnull
  List<SpendEntity> findAllByUsername(String username);

  @Nonnull
  Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description
  );

  @Nonnull
  List<SpendEntity> findAll();

  void remove(SpendEntity spend);

  void removeCategory(CategoryEntity category);
}
