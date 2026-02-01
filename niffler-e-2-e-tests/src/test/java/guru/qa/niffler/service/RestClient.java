package guru.qa.niffler.service;

import static org.apache.commons.lang.ArrayUtils.isNotEmpty;

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
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ParametersAreNonnullByDefault
public abstract class RestClient {

  private static final String HTTP_REQUEST_TEMPLATE = "http-request.ftl";
  private static final String HTTP_RESPONSE_TEMPLATE = "http-response.ftl";
  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient client;
  private final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS,
        null);
  }

  public RestClient(String baseUrl, boolean followRedirect) {
    this(baseUrl, followRedirect, JacksonConverterFactory.create(),
        HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
    this(baseUrl,
        followRedirect,
        JacksonConverterFactory.create(),
        interceptors);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory) {
    this(baseUrl, followRedirect, converterFactory, HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, Converter.Factory converterFactory) {
    this(baseUrl, false, converterFactory, HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory,
      Interceptor... interceptors) {
    this(baseUrl, followRedirect, converterFactory, HttpLoggingInterceptor.Level.HEADERS,
        interceptors);
  }

  public RestClient(
      String baseUrl,
      boolean followRedirect,
      Converter.Factory converterFactory,
      HttpLoggingInterceptor.Level level,
      @Nullable Interceptor... interceptors) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
        .followRedirects(followRedirect);

    if (isNotEmpty(interceptors)) {
      for (Interceptor interceptor : interceptors) {
        clientBuilder.addNetworkInterceptor(interceptor);
      }
    }

    clientBuilder
        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level))
        .addNetworkInterceptor(
            new AllureOkHttp3().setRequestTemplate(HTTP_REQUEST_TEMPLATE)
                .setResponseTemplate(HTTP_RESPONSE_TEMPLATE)
        )
        .cookieJar(
            new JavaNetCookieJar(
                new CookieManager(
                    ThreadSafeCookieStore.INSTANCE,
                    CookiePolicy.ACCEPT_ALL
                )

            )
        );

    this.client = clientBuilder.build();
    this.retrofit = new Retrofit.Builder()
        .client(this.client)
        .baseUrl(baseUrl)
        .addConverterFactory(converterFactory)
        .build();
  }

  public <T> T create(Class<T> serviceClass) {
    return retrofit.create(serviceClass);
  }


  @ParametersAreNonnullByDefault
  public static final class EmtyRestClient extends RestClient {

    public EmtyRestClient(String baseUrl) {
      super(baseUrl);
    }

    public EmtyRestClient(String baseUrl, boolean followRedirect) {
      super(baseUrl, followRedirect);
    }

    public EmtyRestClient(String baseUrl, Converter.Factory factory) {
      super(baseUrl, factory);
    }

    public EmtyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory,
        HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
      super(baseUrl, followRedirect, factory, level, interceptors);
    }
  }
}
