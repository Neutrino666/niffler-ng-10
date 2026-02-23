package guru.qa.niffler.api.rest.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpendApi {

  @GET("internal/spends/{id}")
  @Nonnull
  Call<SpendJson> getSpendById(
      @Path("id") String id,
      @Query("username") String username
  );

  @GET("internal/spends/all")
  @Nonnull
  Call<List<SpendJson>> getAllSpends(
      @Query("username") String username,
      @Query("filterCurrency") CurrencyValues currencyValue,
      @Query("from") Date from,
      @Query("to") Date to
  );

  @POST("internal/spends/add")
  @Nonnull
  Call<SpendJson> createSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  @Nonnull
  Call<SpendJson> update(@Body SpendJson spend);

  @DELETE("internal/spends/remove")
  @Nonnull
  Call<Void> removeSpends(
      @Query("username") String username,
      @Query("ids") List<String> ids
  );
}
