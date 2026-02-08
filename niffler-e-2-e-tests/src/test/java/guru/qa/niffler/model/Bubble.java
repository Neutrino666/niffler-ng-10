package guru.qa.niffler.model;

import guru.qa.niffler.condition.Color;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record Bubble(Color color,
                     String text) {

  @Override
  @Nonnull
  public String toString() {
    return List.of(color.getRgb(), text)
        .toString();
  }
}
