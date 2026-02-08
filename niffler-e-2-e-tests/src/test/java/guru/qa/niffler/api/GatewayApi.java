package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.SessionJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.StatisticJson;
import guru.qa.niffler.model.UserJson;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

@ParametersAreNonnullByDefault
public interface GatewayApi {

  @GET("api/categories/all")
  @Nonnull
  Call<List<CategoryJson>> allCategories(@Header("Authorization") String bearerToken);

  @POST("api/categories/add")
  @Nonnull
  Call<CategoryJson> addCategory(@Header("Authorization") String bearerToken,
      @Body CategoryJson category);

  @GET("api/currencies/all")
  @Nonnull
  Call<List<CurrencyJson>> allCurrencies(@Header("Authorization") String bearerToken);

  @GET("api/friends/all")
  @Nonnull
  Call<List<UserJson>> allFriends(@Header("Authorization") String bearerToken,
      @Query("searchQuery") @Nullable String searchQuery);

  @DELETE("api/friends/remove")
  @Nonnull
  Call<Void> removeFriend(@Header("Authorization") String bearerToken,
      @Query("username") @Nullable String targetUsername);

  @POST("api/invitations/send")
  @Nonnull
  Call<UserJson> sendInvitation(@Header("Authorization") String bearerToken,
      @Body FriendJson friend);

  @POST("api/invitations/accept")
  @Nonnull
  Call<UserJson> acceptInvitation(@Header("Authorization") String bearerToken,
      @Body FriendJson friend);

  @POST("api/invitations/decline")
  @Nonnull
  Call<UserJson> declineInvitation(@Header("Authorization") String bearerToken,
      @Body FriendJson friend);

  @GET("api/session/current")
  @Nonnull
  Call<SessionJson> currentSession(@Header("Authorization") String bearerToken);

  @GET("api/spends/all")
  @Nonnull
  Call<List<SpendJson>> allSpends(@Header("Authorization") String bearerToken,
      @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
      @Query("filterPeriod") @Nullable DataFilterValues filterPeriod);

  @POST("api/spends/add")
  @Nonnull
  Call<SpendJson> addSpend(@Header("Authorization") String bearerToken,
      @Body SpendJson spend);

  @PATCH("api/spends/edit")
  @Nonnull
  Call<SpendJson> editSpend(@Header("Authorization") String bearerToken,
      @Body SpendJson spend);

  @DELETE("api/spends/remove")
  @Nonnull
  Call<Void> removeSpends(@Header("Authorization") String bearerToken,
      @Query("ids") List<String> ids);

  @GET("api/stat/total")
  @Nonnull
  Call<List<StatisticJson>> totalStat(@Header("Authorization") String bearerToken,
      @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
      @Query("filterPeriod") @Nullable DataFilterValues filterPeriod);

  @GET("api/users/current")
  @Nonnull
  Call<UserJson> currentUser(@Header("Authorization") String bearerToken);

  @GET("api/users/all")
  @Nonnull
  Call<List<UserJson>> allUsers(@Header("Authorization") String bearerToken,
      @Query("searchQuery") @Nullable String searchQuery);

  @POST("api/users/update")
  @Nonnull
  Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
      @Body UserJson user);
}
