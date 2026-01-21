package guru.qa.niffler.service.user;

import guru.qa.niffler.api.user.AuthApi;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ParametersAreNonnullByDefault
public class AuthApiClient {

  private static final CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

  @Nonnull
  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("https://auth.niffler-stage.qa.guru/")
      .addConverterFactory(JacksonConverterFactory.create())
      .client(new OkHttpClient.Builder()
          .cookieJar(new JavaNetCookieJar(
              cm
          ))
          .build())
      .build();

  @Nonnull
  private final AuthApi authApi = retrofit.create(AuthApi.class);

  @Nonnull
  public Response<Void> register(String username, String password) throws IOException {
    authApi.requestRegisterForm().execute();
    return authApi.register(
        username,
        password,
        password,
        cm.getCookieStore().getCookies()
            .stream()
            .filter(c -> c.getName().equals("XSRF-TOKEN"))
            .findFirst()
            .get()
            .getValue()
    ).execute();
  }
}
