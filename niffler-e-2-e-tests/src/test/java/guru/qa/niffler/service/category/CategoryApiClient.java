package guru.qa.niffler.service.category;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.spend.CategoryApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ParametersAreNonnullByDefault
public class CategoryApiClient implements CategoryClient {

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final CategoryApi categoryApi = retrofit.create(CategoryApi.class);

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
  @Nonnull
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
    return Objects.requireNonNull(
        response.body()
    );
  }

  @Nonnull
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
    return Objects.requireNonNull(
        response.body()
    );
  }
}
