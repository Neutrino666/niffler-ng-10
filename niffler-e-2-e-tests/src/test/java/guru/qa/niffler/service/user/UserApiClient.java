package guru.qa.niffler.service.user;

import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.user.AuthApi;
import guru.qa.niffler.api.user.UserdataApi;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.assertj.core.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ParametersAreNonnullByDefault
public class UserApiClient extends RestClient implements UserClient {

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

  private final AuthApi authApi = authRetrofit.create(AuthApi.class);
  private final UserdataApi userdataApi;

  public UserApiClient() {
    super(CFG.userdataUrl());
    userdataApi = create(UserdataApi.class);
  }

  @Nonnull
  @Override
  @Step("REST API Регистрация нового пользователя")
  public UserJson create(String username, String password) {
    final Response<Void> response;
    try {
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
      Stopwatch sw = Stopwatch.createStarted();
      int waitTime = 10;
      while (sw.elapsed(TimeUnit.SECONDS) < waitTime) {
        UserJson userJson = userdataApi.currentUser(username)
            .execute()
            .body();
        if (userJson != null && userJson.id() != null) {
          return userJson;
        } else {
          Thread.sleep(100);
        }
      }
    } catch (IOException e) {
      throw new AssertionError(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

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
  @Step("REST API Поиск пользователя по username")
  public Optional<UserJson> findByUsername(String username) {
    final Response<UserJson> response;
    if (username.isEmpty()) {
      throw new RuntimeException("Empty user");
    }
    try {
      response = userdataApi.currentUser(username)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code()).isEqualTo(SC_OK);
    return Optional.ofNullable(
        response.body()
    );
  }

  @Override
  @Nonnull
  @Step("REST API Создание входящих запросов дружбы")
  public List<UserJson> createIncomeInvitation(UserJson targetUser, int count) {
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
      try {
        response = userdataApi.sendInvitation(user.username(), targetUser.username())
            .execute();
      } catch (IOException e) {
        throw new AssertionError(e);
      }
      Assertions.assertThat(response.code()).isEqualTo(SC_OK);
      result.add(user);
    }
    return result;
  }

  @Override
  @Nonnull
  @Step("REST API Создание исходящих запросов дружбы")
  public List<UserJson> createOutcomeInvitation(UserJson targetUser, int count) {
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
      try {
        response = userdataApi.sendInvitation(targetUser.username(), user.username())
            .execute();
      } catch (IOException e) {
        throw new AssertionError(e);
      }
      Assertions.assertThat(response.code()).isEqualTo(SC_OK);
      result.add(user);
    }
    return result;
  }

  @Override
  @Nonnull
  @Step("REST API Создание друзей пользователю")
  public List<UserJson> createFriends(UserJson targetUser, int count) {
    List<UserJson> result = new ArrayList<>();
    if (count < 0) {
      throw new RuntimeException("wrong count: " + count);
    }
    for (int i = 0; i < count; i++) {
      final Response<UserJson> response;
      final UserJson user = createIncomeInvitation(targetUser, 1).getFirst();
      try {
        response = userdataApi.acceptInvitation(targetUser.username(), user.username())
            .execute();
      } catch (IOException e) {
        throw new AssertionError(e);
      }
      Assertions.assertThat(response.code()).isEqualTo(SC_OK);
      result.add(user);
    }
    return result;
  }

  @Nonnull
  @Step("REST API Получение всех пользователей")
  public List<UserJson> getAllUsers(String username, String searchQuery) {
    try {
      List<UserJson> result = userdataApi.allUsers(username, searchQuery)
          .execute().
          body();
      return result != null
          ? result
          : List.of();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  @Step("REST API Удаление пользователя")
  public void delete(UserJson user) {
    throw new RuntimeException("Not implemented :(");
  }
}