package guru.qa.niffler.service.spend;

import static org.apache.hc.core5.http.HttpStatus.SC_ACCEPTED;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.rest.spend.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.category.CategoryApiClient;
import guru.qa.niffler.service.category.CategoryClient;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient implements SpendClient {

  private final SpendApi spendApi;
  private final CategoryClient categoryClient = new CategoryApiClient();

  public SpendApiClient() {
    super(CFG.spendUrl());
    this.spendApi = create(SpendApi.class);
  }

  @Nullable
  @Step("REST API Получение траты по id")
  public SpendJson getById(String id, String username) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpendById(id, username)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

  @Nonnull
  @Step("REST API получение трат пользователя")
  public List<SpendJson> getAll(
      String username,
      @Nullable CurrencyValues currencyValue,
      @Nullable Date from,
      @Nullable Date to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getAllSpends(username, currencyValue, from, to)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }

    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body() != null ? response.body() : List.of();
  }

  @Nullable
  @Override
  @Step("REST API Создание траты")
  public SpendJson create(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.createSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_CREATED);
    return response.body();
  }

  @Nullable
  @Override
  @Step("REST API Обновление траты")
  public SpendJson update(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.update(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

  @Nonnull
  @Override
  public List<SpendJson> findAllByUsername(String username) {
    return getAll(username, null, null, null);
  }

  @Override
  public @Nonnull List<CategoryJson> findAllCategoryByUsername(String username) {
    return categoryClient.findAllByUsername(username);
  }

  @Override
  @Step("REST API Удаление траты")
  public void remove(SpendJson spend) {
    remove(spend.username(), List.of(spend.id().toString()));
  }

  @Step("REST API Удаление траты")
  public void remove(String username, List<String> ids) {
    final Response<Void> response;
    try {
      response = spendApi.removeSpends(username, ids)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_ACCEPTED);
  }
}