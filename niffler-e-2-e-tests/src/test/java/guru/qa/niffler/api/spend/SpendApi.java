package guru.qa.niffler.api.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpendApi {

  // Spend controller
  @GET("internal/spends/{id}")
  Call<SpendJson> getSpendById(@Path("id") String id,
      @Query("username") String username);

  @GET("internal/spends/all")
  Call<List<SpendJson>> getAllSpends(@Query("username") String username,
      @Query("filterCurrency") CurrencyValues currencyValue,
      @Query("from") Date from,
      @Query("to") Date to);

  @POST("internal/spends/add")
  Call<SpendJson> createSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);

  @DELETE("internal/spends/remove")
  Call<Void> removeSpends(@Query("username") String username,
      @Query("ids") List<String> ids);
}
