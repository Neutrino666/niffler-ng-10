package guru.qa.niffler.api.user;

import guru.qa.niffler.model.UserJson;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@ParametersAreNonnullByDefault
public interface UserdataApi {

  @POST("internal/invitations/send")
  Call<UserJson> sendInvitation(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );

  @POST("internal/invitations/accept")
  Call<UserJson> acceptInvitation(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );

  @GET("internal/users/all")
  Call<List<UserJson>> allUsers(
      @Query("username") String username,
      @Query("searchQuery") @Nullable String searchQuery
  );

  @GET("internal/friends/all")
  Call<List<UserJson>> allFriends(@Query("username") String username,
      @Query("searchQuery") @Nullable String searchQuery);

  @GET("internal/users/current")
  Call<UserJson> currentUser(@Query("username") String username);
}
