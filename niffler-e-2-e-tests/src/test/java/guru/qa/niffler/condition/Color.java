package guru.qa.niffler.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
  YELLOW("rgba(255, 183, 3, 1)"),
  GREEN("rgba(53, 173, 123 1)");

  private final String rgb;
}
