package guru.qa.niffler.service.spend;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface SpendClient {

  @Nonnull
  SpendJson create(@Nonnull SpendJson spend);

  @Nonnull
  SpendJson update(@Nonnull SpendJson spend);

  @Nonnull
  CategoryJson updateCategory(@Nonnull CategoryJson category);

  @Nonnull
  CategoryJson createCategory(@Nonnull CategoryJson category);

  @Nonnull
  Optional<CategoryJson> findCategoryById(@Nonnull UUID id);

  @Nonnull
  Optional<CategoryJson> findCategoryByUsernameAndSpendName(
      @Nonnull String username,
      @Nonnull String name
  );

  @Nonnull
  Optional<SpendJson> findById(@Nonnull UUID id);

  @Nonnull
  List<SpendJson> findAllByUsername(@Nonnull String username);

  @Nonnull
  Optional<SpendJson> findByUsernameAndSpendDescription(
      @Nonnull String username,
      @Nonnull String description
  );

  @Nonnull
  List<SpendJson> findAll();

  void remove(@Nonnull SpendJson spend);

  void removeCategory(@Nonnull CategoryJson category);
}
