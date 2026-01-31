package guru.qa.niffler.service;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import java.net.CookieManager;
import java.net.CookiePolicy;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ParametersAreNonnullByDefault
public abstract class RestClient {

  private static final String httpRequestTemplate = "http-request.ftl";
  private static final String httpResponseTemplate = "http-response.ftl";
  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient okHttpClient;
  private final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl,
        JacksonConverterFactory.create(),
        true,
        null);
  }

  public RestClient(String baseUrl, boolean followRedirect) {
    this(baseUrl,
        JacksonConverterFactory.create(),
        followRedirect,
        null);
  }

  public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
    this(baseUrl,
        JacksonConverterFactory.create(),
        followRedirect,
        interceptors);
  }

  public RestClient(
      String baseUrl,
      Converter.Factory converterFactory,
      boolean followRedirect,
      @Nullable Interceptor... interceptors
  ) {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .followRedirects(followRedirect)
        .cookieJar(
            new JavaNetCookieJar(
                new CookieManager(
                    ThreadSafeCookieStore.INSTANCE,
                    CookiePolicy.ACCEPT_ALL
                )
            )
        );
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        builder.addNetworkInterceptor(interceptor);
      }
    }
    builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(Level.BASIC));
    builder.addNetworkInterceptor(
        new AllureOkHttp3().setRequestTemplate(httpRequestTemplate)
            .setResponseTemplate(httpResponseTemplate)
    );
    this.okHttpClient = builder.build();
    this.retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(converterFactory)
        .client(okHttpClient)
        .build();
  }

  public <T> T create(Class<T> serviceClass) {
    return retrofit.create(serviceClass);
  }
}
