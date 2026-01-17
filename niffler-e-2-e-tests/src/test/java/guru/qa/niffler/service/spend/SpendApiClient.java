package guru.qa.niffler.service.spend;

import static org.apache.hc.core5.http.HttpStatus.SC_ACCEPTED;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.spend.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.assertj.core.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SpendApiClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .client(new OkHttpClient.Builder()
          .addInterceptor(new AllureOkHttp3())
          .build())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @SneakyThrows
  public SpendJson getById(String id, String username) {
    final Response<SpendJson> response;
    response = spendApi.getSpendById(id, username)
        .execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

  @SneakyThrows
  public List<SpendJson> getAll(String username, CurrencyValues currencyValue, Date from,
      Date to) {
    final Response<List<SpendJson>> response;
    response = spendApi.getAllSpends(username, currencyValue, from, to)
        .execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

  @Nonnull
  @Override
  @SneakyThrows
  public SpendJson create(@Nonnull SpendJson spend) {
    final Response<SpendJson> response;
    response = spendApi.createSpend(spend)
        .execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_CREATED);
    return response.body();
  }

  @Nonnull
  @Override
  @SneakyThrows
  public SpendJson update(@Nonnull SpendJson spend) {
    final Response<SpendJson> response;
    response = spendApi.update(spend)
        .execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

  @Nonnull
  @Override
  public List<SpendJson> findAllByUsername(@Nonnull String username) {
    return getAll(username, null, null, null);
  }

  @Override
  public void remove(@Nonnull SpendJson spend) {
    remove(spend.username(), List.of(spend.id().toString()));
  }

  @SneakyThrows
  public void remove(String username, List<String> ids) {
    final Response<Void> response;
    response = spendApi.removeSpends(username, ids)
        .execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_ACCEPTED);
  }
}