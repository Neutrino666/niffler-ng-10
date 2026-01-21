package guru.qa.niffler.service.category;

import guru.qa.niffler.model.CategoryJson;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CategoryClient {

  @Nullable
  CategoryJson create(CategoryJson category);

  @Nullable
  CategoryJson update(CategoryJson category);
}
