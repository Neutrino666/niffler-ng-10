package guru.qa.niffler.page.components;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum DataFilterValues {
  TODAY("Today"),
  WEEK("Last week"),
  MONTH("Last month"),
  ALL("All time");

  private final String value;
}

