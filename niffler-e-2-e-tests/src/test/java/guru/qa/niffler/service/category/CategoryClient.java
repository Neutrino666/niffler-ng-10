package guru.qa.niffler.service.category;

import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CategoryClient {

  @Nonnull
  CategoryJson create(CategoryJson category);

  @Nonnull
  CategoryJson update(CategoryJson category);

  @Nonnull
  List<CategoryJson> findAllByUsername(String username);
}
