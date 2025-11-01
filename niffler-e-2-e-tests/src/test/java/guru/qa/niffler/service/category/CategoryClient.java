package guru.qa.niffler.service.category;

import guru.qa.niffler.model.CategoryJson;

public interface CategoryClient {

  CategoryJson create(CategoryJson category);

  CategoryJson update(CategoryJson category);
}
