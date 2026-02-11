package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.CurrenciesQuery.Currency;
import guru.qa.CurrenciesQuery.Data;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GRAPHQL Currency")
public class CurrencyGraphQlTest extends BaseGraphQlTest {

  @User
  @Test
  @ApiLogin
  @DisplayName("Все валюты должны присутствовать")
  void allCurrencyShouldBeReturnedFromGateway(@Token String bearerToken) {
    final ApolloCall<Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
        .addHttpHeader("authorization", bearerToken);

    final ApolloResponse<Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
    final Data data = response.dataOrThrow();
    List<Currency> actual = data.currencies;
    List<String> expected = Stream.of(CurrencyValues.values())
        .map(CurrencyValues::name)
        .toList();
    Assertions.assertThat(actual)
        .extracting(c -> c.currency.rawValue)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(expected);
  }
}
