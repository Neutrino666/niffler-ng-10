package guru.qa.niffler.service.category;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.spend.CategoryApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public class CategoryApiClient extends RestClient implements CategoryClient {

  private final CategoryApi categoryApi;

  public CategoryApiClient() {
    super(CFG.spendUrl());
    categoryApi = create(CategoryApi.class);
  }

  @Nonnull
  @Step("REST API Получение категорий пользователя с фильтрацией по признаку в архиве")
  public List<CategoryJson> getAllByUsername(String username, boolean excludeArchived) {
    final Response<List<CategoryJson>> response;
    try {
      response = categoryApi.getCategories(username, excludeArchived)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
    return response.body() != null ? response.body() : List.of();
  }

  @Override
  @Nullable
  @Step("REST API Создание категории")
  public CategoryJson create(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = categoryApi.createCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
    return response.body();
  }

  @Nullable
  @Step("REST API Обновление категории")
  public CategoryJson update(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = categoryApi.updateCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
    return response.body();
  }
}
