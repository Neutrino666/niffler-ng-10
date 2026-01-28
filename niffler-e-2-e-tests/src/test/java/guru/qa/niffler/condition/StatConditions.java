package guru.qa.niffler.condition;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.openqa.selenium.WebElement;

@ParametersAreNonnullByDefault
public class StatConditions {

  public static WebElementCondition color(Color expectedColor) {

    return new WebElementCondition("color") {
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final String rgba = element.getCssValue("background-color");
        return new CheckResult(
            expectedColor.getRgb()
                .equals(rgba),
            rgba
        );
      }
    };
  }

  public static WebElementsCondition colors(Color... expectedColors) {

    return new WebElementsCondition() {
      private final String expected = Stream.of(expectedColors)
          .map(Color::getRgb)
          .toList()
          .toString();

      @Override
      public @Nonnull String toString() {
        return expected;
      }

      @Override
      public @Nonnull CheckResult check(Driver driver, List<WebElement> elements) {

        if (ArrayUtils.isEmpty(expectedColors)) {
          throw new IllegalArgumentException("No expected colors given");
        }
        if (expectedColors.length != elements.size()) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)",
              expectedColors.length, elements.size());
          String actual = elements
              .stream()
              .map(el -> el.getCssValue("background-color"))
              .toList().toString();
          return rejected(message, actual);
        }

        boolean passed = true;
        List<String> actualRgba = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
          final WebElement el = elements.get(i);
          final Color color = expectedColors[i];
          final String rgba = el.getCssValue("background-color");
          actualRgba.add(rgba);
          if (passed) {
            passed = color.getRgb().equals(rgba);
          }
        }

        if (!passed) {
          final String actual = actualRgba.toString();
          final String message = String.format("List colors mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual);
        }

        return accepted();
      }
    };
  }
}
