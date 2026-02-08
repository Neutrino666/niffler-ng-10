package guru.qa.niffler.model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public enum CurrencyValues {
  RUB("₽"),
  USD("$"),
  EUR("€"),
  KZT("₸");

  private @Nonnull
  final String value;
}
