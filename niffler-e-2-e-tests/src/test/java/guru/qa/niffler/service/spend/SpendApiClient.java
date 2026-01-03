package guru.qa.niffler.service.spend;

import static org.apache.hc.core5.http.HttpStatus.SC_ACCEPTED;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.spend.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import org.assertj.core.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SpendApiClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

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

  public List<SpendJson> getAll(String username, CurrencyValues currencyValue, Date from,
      Date to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getAllSpends(username, currencyValue, from, to)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

  @Override
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

  @Nonnull
  @Override
  public SpendJson update(@Nonnull SpendJson spend) {
    throw new RuntimeException("Not implemented ):");
  }

  @Nonnull
  @Override
  public List<SpendJson> findAllByUsername(@Nonnull String username) {
    throw new RuntimeException("Not implemented ):");
  }

  @Override
  public void remove(@Nonnull SpendJson spend) {
    throw new RuntimeException("Not implemented ):");
  }

  public SpendJson edit(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return response.body();
  }

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