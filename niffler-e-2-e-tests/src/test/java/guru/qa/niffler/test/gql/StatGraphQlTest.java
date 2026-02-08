package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.StatQuery.Data;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Stat")
public class StatGraphQlTest extends BaseGraphQlTest {

  @User
  @Test
  @ApiLogin
  @DisplayName("Траты должны отсутствовать")
  void totalStatShouldBeZero(@Token String bearerToken) {
    final ApolloCall<Data> currenciesCall = apolloClient.query(
            StatQuery.builder()
                .filterCurrency(null)
                .filterPeriod(null)
                .statCurrency(null)
                .build()
        )
        .addHttpHeader("authorization", bearerToken);

    final ApolloResponse<Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
    final Data data = response.dataOrThrow();
    StatQuery.Stat actual = data.stat;
    Assertions.assertThat(actual)
        .extracting(s -> s.total)
        .isEqualTo(0.0);
  }
}
