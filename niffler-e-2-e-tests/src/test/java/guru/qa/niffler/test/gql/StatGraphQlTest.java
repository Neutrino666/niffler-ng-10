package guru.qa.niffler.test.gql;

import static guru.qa.type.CurrencyValues.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.StatQuery.Data;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GRAPHQL Stat")
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
    assertThat(actual)
        .extracting(s -> s.total)
        .isEqualTo(0.0);
  }

  @User(
      categories = {
          @Category(name = "active"),
          @Category(name = "archive", archived = true)
      },
      spendings = {
          @Spending(category = "active", description = "active description", amount = 1., currency = CurrencyValues.USD),
          @Spending(category = "archive", description = "archive description", amount = 1.)
      }
  )
  @Test
  @ApiLogin
  @DisplayName("Валидация трат архивна + активная")
  void archiveAndActiveSpendingIsPresent(@Token String bearerToken) {
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
    assertAll(
        () -> Assertions.assertThat(actual)
            .as("Общая валидация статистики")
            .hasFieldOrPropertyWithValue("currency.rawValue", CurrencyValues.RUB.name())
            .hasFieldOrPropertyWithValue("total", 67.67)
            .hasFieldOrProperty("statByCategories"),
        () -> Assertions.assertThat(actual.statByCategories.getFirst())
            .as("Валидация первой траты")
            .hasFieldOrPropertyWithValue("categoryName", "active")
            .hasFieldOrPropertyWithValue("currency.rawValue", CurrencyValues.RUB.name())
            .hasFieldOrPropertyWithValue("sum", 66.67),
        () -> Assertions.assertThat(actual.statByCategories.getLast())
            .as("Валидация второй траты")
            .hasFieldOrPropertyWithValue("categoryName", "Archived")
            .hasFieldOrPropertyWithValue("currency.rawValue", CurrencyValues.RUB.name())
            .hasFieldOrPropertyWithValue("sum", 1.)
    );
  }

  @User(
      spendings = {
          @Spending(category = "ru", description = "ru description", amount = 10.),
          @Spending(category = "usd", description = "usd description", amount = 1., currency = CurrencyValues.USD),
          @Spending(category = "eur", description = "eur description", amount = 10000., currency = CurrencyValues.EUR),
          @Spending(category = "kzt", description = "kzt description", amount = 135356643., currency = CurrencyValues.KZT),
      }
  )
  @Test
  @ApiLogin
  @DisplayName("Фильтрация трат при наличии всех наминалов")
  void allNominalSpendsIsPresent(@Token String bearerToken) {
    final ApolloCall<Data> currenciesCall = apolloClient.query(
            StatQuery.builder()
                .filterCurrency(USD)
                .filterPeriod(null)
                .statCurrency(USD)
                .build()
        )
        .addHttpHeader("authorization", bearerToken);
    final ApolloResponse<Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
    final Data data = response.dataOrThrow();
    StatQuery.Stat actual = data.stat;
    assertAll(
        () -> Assertions.assertThat(actual)
            .as("Общая валидация статистики")
            .hasFieldOrPropertyWithValue("currency.rawValue", CurrencyValues.USD.name())
            .hasFieldOrPropertyWithValue("total", 1.)
            .hasFieldOrProperty("statByCategories"),
        () -> Assertions.assertThat(actual.statByCategories.getFirst())
            .as("Валидация первой траты")
            .hasFieldOrPropertyWithValue("categoryName", "usd")
            .hasFieldOrPropertyWithValue("currency.rawValue", CurrencyValues.USD.name())
            .hasFieldOrPropertyWithValue("sum", 1.),
        () -> Assertions.assertThat(actual.statByCategories)
            .as("Валидация количества отлфильтрованных трат")
            .hasSize(1)
    );
  }
}
