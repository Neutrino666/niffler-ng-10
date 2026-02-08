package guru.qa.niffler.condition;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public enum Color {
  YELLOW("rgba(255, 183, 3, 1)"),
  GREEN("rgba(53, 173, 123, 1)"),
  BLUE100("rgba(41, 65, 204, 1)"),
  ORANGE("rgba(251, 133, 0, 1)"),
  AZURE("rgba(33, 158, 188, 1)"),
  BLUE200("rgba(22, 41, 149, 1)"),
  RED("rgba(247, 89, 67, 1)"),
  SKY_BLUE("rgba(99, 181, 226, 1)"),
  PURPLE("rgba(148, 85, 198, 1)");

  public @Nonnull
  final String rgb;

  @Override
  public @Nonnull String toString() {
    return this.rgb;
  }
}
