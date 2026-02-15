package guru.qa.niffler.test.grpc;

import static guru.qa.niffler.grpc.CurrencyValues.EUR;
import static guru.qa.niffler.grpc.CurrencyValues.KZT;
import static guru.qa.niffler.grpc.CurrencyValues.RUB;
import static guru.qa.niffler.grpc.CurrencyValues.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.model.CurrencyValues;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("gRPS Currency")
public class CurrencyGrpcTest extends BaseGrpcTest {

  @Test
  @DisplayName("Все номиналы")
  void allCurrenciesShouldBeReturned() {
    final CurrencyResponse response = currencyBlockingStub.getAllCurrencies(
        Empty.getDefaultInstance());
    final List<String> actual = response.getAllCurrenciesList()
        .stream()
        .map(c -> c.getCurrency().name())
        .toList();
    final List<String> expected = Stream.of(CurrencyValues.values())
        .map(CurrencyValues::name)
        .toList();
    assertThat(actual)
        .as("Количество номиналов соответствует")
        .hasSize(CurrencyValues.values().length)
        .as("Присутствует только корректные номиналы")
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  public static Stream<Arguments> argsProvider() {
    return Stream.of(
        arguments(EUR, RUB, 100., 7200.0),
        arguments(KZT, RUB, 100., 14.0),
        arguments(USD, RUB, 100., 6666.67),
        arguments(RUB, RUB, 100., 100.0),

        arguments(KZT, EUR, 100., 0.19),
        arguments(USD, EUR, 100., 92.59),
        arguments(RUB, EUR, 100., 1.39),
        arguments(EUR, EUR, 100., 100.0),

        arguments(USD, KZT, 100., 47619.05),
        arguments(RUB, KZT, 100., 714.29),
        arguments(EUR, KZT, 100., 51428.57),
        arguments(KZT, KZT, 100., 100.0),

        arguments(RUB, USD, 100., 1.5),
        arguments(EUR, USD, 100., 108.0),
        arguments(KZT, USD, 100., 0.21),
        arguments(USD, USD, 100., 100.0),

        arguments(RUB, USD, .01, 0.),
        arguments(EUR, USD, .01, 0.01),
        arguments(KZT, USD, .01, 0.),
        arguments(USD, USD, .01, .01)
    );
  }

  @ParameterizedTest(name = "Конвертация траты: из {0} в {1} суммы: {2}, ожидается {3} {1}")
  @MethodSource("argsProvider")
  void convertCurrencyShouldBeValid(
      guru.qa.niffler.grpc.CurrencyValues from,
      guru.qa.niffler.grpc.CurrencyValues to,
      double amount,
      double expected
  ) {
    final CalculateResponse response = currencyBlockingStub.calculateRate(
        CalculateRequest.newBuilder()
            .setAmount(amount)
            .setSpendCurrency(from)
            .setDesiredCurrency(to)
            .build());
    assertThat(response.getCalculatedAmount())
        .as("Валидируем конвертацию")
        .isGreaterThanOrEqualTo(0.0d)
        .isEqualTo(expected);
  }
}
