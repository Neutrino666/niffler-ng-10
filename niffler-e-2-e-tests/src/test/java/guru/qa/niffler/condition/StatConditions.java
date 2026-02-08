package guru.qa.niffler.condition;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.niffler.model.Bubble;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.openqa.selenium.WebElement;

@ParametersAreNonnullByDefault
public final class StatConditions {

  public static @Nonnull WebElementCondition color(final Color expectedColor) {

    return new WebElementCondition("color") {
      @Override
      public @Nonnull CheckResult check(final Driver driver, final WebElement element) {
        final String rgba = element.getCssValue("background-color");
        return new CheckResult(
            expectedColor.getRgb()
                .equals(rgba),
            rgba
        );
      }
    };
  }

  public static @Nonnull WebElementsCondition statBubbles(final Bubble... bubbles) {

    return new WebElementsCondition() {
      private final List<String> expected = convertToExpectedStat(bubbles);

      @Override
      public @Nonnull String toString() {
        return expected.toString();
      }

      @Override
      public @Nonnull CheckResult check(final Driver driver, final List<WebElement> elements) {

        if (ArrayUtils.isEmpty(bubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }
        List<String> actual = convertToActualStat(elements);
        if (bubbles.length != elements.size()) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)",
              bubbles.length, elements.size());
          return rejected(message, actual.toString());
        }

        if (!expected.toString().equals(actual.toString())) {
          final String message = String.format("List bubbles mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual.toString());
        }
        return accepted();
      }
    };
  }

  public static @Nonnull WebElementsCondition statBubblesInAnyOrder(final Bubble... bubbles) {

    return new WebElementsCondition() {
      private final List<String> expected = convertToExpectedStat(bubbles);

      @Override
      public @Nonnull String toString() {
        return expected.toString();
      }

      @Override
      public @Nonnull CheckResult check(final Driver driver, final List<WebElement> elements) {

        if (ArrayUtils.isEmpty(bubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }
        List<String> actual = convertToActualStat(elements);
        if (bubbles.length != elements.size()) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)",
              bubbles.length, elements.size());
          return rejected(message, actual.toString());
        }

        if (!actual.containsAll(expected)) {
          final String message = String.format("List bubbles mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual.toString());
        }
        return accepted();
      }
    };
  }

  public static @Nonnull WebElementsCondition statBubblesContains(final Bubble... bubbles) {

    return new WebElementsCondition() {
      private final List<String> expected = convertToExpectedStat(bubbles);

      @Override
      public @Nonnull String toString() {
        return expected.toString();
      }

      @Override
      public @Nonnull CheckResult check(Driver driver, List<WebElement> elements) {

        if (ArrayUtils.isEmpty(bubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }
        List<String> actual = convertToActualStat(elements);

        if (!actual.containsAll(expected)) {
          final String message = String.format("List bubbles mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual.toString());
        }
        return accepted();
      }
    };
  }

  private static @Nonnull List<String> convertToActualStat(final List<WebElement> elements) {
    return elements
        .stream()
        .map(el -> List.of(
                el.getCssValue("background-color"),
                el.getText())
            .toString())
        .toList();
  }

  private static @Nonnull List<String> convertToExpectedStat(final Bubble... bubbles) {
    return Stream.of(bubbles)
        .map(Bubble::toString)
        .toList();
  }
}
