package guru.qa.niffler.service.user;

import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.assertj.core.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserApiClient implements UserClient {

  private static final Config CFG = Config.getInstance();
  private static final CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

  private final Retrofit authRetrofit = new Retrofit.Builder()
      .baseUrl(CFG.authUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .client(new OkHttpClient.Builder()
          .addInterceptor(new AllureOkHttp3())
          .cookieJar(new JavaNetCookieJar(
              cm
          ))
          .build())
      .build();
  private final Retrofit userdataRetrofit = new Retrofit.Builder()
      .baseUrl(CFG.userdataUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .client(new OkHttpClient.Builder()
          .addInterceptor(new AllureOkHttp3())
          .build())
      .build();

  private final AuthApi authApi = authRetrofit.create(AuthApi.class);
  private final UserdataApi userdataApi = userdataRetrofit.create(UserdataApi.class);

  @Nonnull
  @Override
  @SneakyThrows
  public UserJson create(@Nonnull String username, @Nonnull String password) {
    final Response<Void> response;
    authApi.requestRegisterForm().execute();
    response = authApi.register(
        username,
        password,
        password,
        cm.getCookieStore().getCookies()
            .stream()
            .filter(c -> c.getName().equals("XSRF-TOKEN"))
            .findFirst()
            .orElseThrow()
            .getValue()
    ).execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_CREATED);
    return new UserJson(
        null,
        username,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );
  }

  @Nonnull
  @Override
  @SneakyThrows
  public Optional<UserJson> findByUsername(@Nonnull String username) {
    final Response<UserJson> response;
    if (username.isEmpty()) {
      throw new RuntimeException("Empty user");
    }
    response = userdataApi.currentUser(username)
        .execute();
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return Optional.ofNullable(
        response.body()
    );
  }

  @Override
  @SneakyThrows
  public List<UserJson> createIncomeInvitation(@Nonnull UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count < 0) {
      throw new RuntimeException("wrong count: " + count);
    }
    for (int i = 0; i < count; i++) {
      final Response<UserJson> response;
      final UserJson user = create(
          RandomDataUtils.getRandomUserName(),
          UserExtension.DEFAULT_PASSWORD
      );
      response = userdataApi.sendInvitation(user.username(), targetUser.username())
          .execute();
      Assertions.assertThat(response.code()).isEqualTo(SC_OK);
      result.add(user);
    }
    return result;
  }

  @Override
  @SneakyThrows
  public List<UserJson> createOutcomeInvitation(@Nonnull UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count < 0) {
      throw new RuntimeException("wrong count: " + count);
    }
    for (int i = 0; i < count; i++) {
      final Response<UserJson> response;
      final UserJson user = create(
          RandomDataUtils.getRandomUserName(),
          UserExtension.DEFAULT_PASSWORD
      );
      response = userdataApi.sendInvitation(targetUser.username(), user.username())
          .execute();
      Assertions.assertThat(response.code()).isEqualTo(SC_OK);
      result.add(user);
    }
    return result;
  }

  @Override
  @SneakyThrows
  public List<UserJson> createFriends(@Nonnull UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count < 0) {
      throw new RuntimeException("wrong count: " + count);
    }
    for (int i = 0; i < count; i++) {
      final Response<UserJson> response;
      final UserJson user = createIncomeInvitation(targetUser, 1).getFirst();
      response = userdataApi.acceptInvitation(targetUser.username(), user.username()).execute();
      Assertions.assertThat(response.code()).isEqualTo(SC_OK);
      result.add(user);
    }
    return result;
  }

  @Override
  public void delete(@Nonnull UserJson user) {
    throw new RuntimeException("Not implemented :(");
  }
}