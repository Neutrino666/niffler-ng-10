package guru.qa.niffler.test.gql;

import com.apollographql.adapter.core.DateAdapter;
import com.apollographql.java.client.ApolloClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.meta.GqlTest;
import guru.qa.type.Date;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.junit.jupiter.api.extension.RegisterExtension;

@GqlTest
public class BaseGraphQlTest {

  @RegisterExtension
  protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

  public static final Config CFG = Config.getInstance();

  protected static final ApolloClient apolloClient = new ApolloClient.Builder()
      .serverUrl(CFG.gatewayUrl() + "graphql")
      .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
      .okHttpClient(
          new OkHttpClient.Builder()
              .addInterceptor(new AllureOkHttp3())
              .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
              .build()
      ).build();
}