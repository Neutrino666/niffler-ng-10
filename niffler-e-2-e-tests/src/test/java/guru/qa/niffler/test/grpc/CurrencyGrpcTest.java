package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.model.CurrencyValues;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("gRPS Currency")
public class CurrencyGrpcTest extends BaseGrpcTest {

  @Test
  @DisplayName("Количество номиналов")
  void allCurrenciesShouldBeReturned() {
    final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
    final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
    Assertions.assertThat(allCurrenciesList)
        .as("Количество номиналов соответвует")
        .hasSize(CurrencyValues.values().length);
  }
}
