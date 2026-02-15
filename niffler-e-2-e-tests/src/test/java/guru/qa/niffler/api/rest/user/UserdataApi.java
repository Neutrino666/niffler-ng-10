package guru.qa.niffler.api.rest.user;

import guru.qa.niffler.model.UserJson;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@ParametersAreNonnullByDefault
public interface UserdataApi {

  @POST("internal/users/update")
  @Nonnull
  Call<UserJson> updateUserInfo(@Body UserJson user);

  @GET("internal/invitations/outcome")
  @Nonnull
  Call<List<UserJson>> outcomeInvitations(@Query("username") String username,
      @Query("searchQuery") @Nullable String searchQuery);

  @POST("internal/invitations/accept")
  @Nonnull
  Call<UserJson> acceptInvitation(@Query("username") String username,
      @Query("targetUsername") String targetUsername);

  @POST("internal/invitations/decline")
  @Nonnull
  Call<UserJson> declineInvitation(@Query("username") String username,
      @Query("targetUsername") String targetUsername);

  @POST("internal/invitations/send")
  @Nonnull
  Call<UserJson> sendInvitation(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );

  @GET("internal/users/all")
  @Nonnull
  Call<List<UserJson>> allUsers(
      @Query("username") String username,
      @Query("searchQuery") @Nullable String searchQuery
  );

  @GET("internal/friends/all")
  @Nonnull
  Call<List<UserJson>> allFriends(@Query("username") String username,
      @Query("searchQuery") @Nullable String searchQuery);

  @GET("internal/users/current")
  @Nonnull
  Call<UserJson> currentUser(@Query("username") String username);

  @DELETE("internal/friends/remove")
  @Nonnull
  Call<Void> removeFriend(@Query("username") String username,
      @Query("targetUsername") String targetUsername);
}
