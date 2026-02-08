package guru.qa.niffler.condition;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.niffler.model.SpendJson;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.opentest4j.AssertionFailedError;

@ParametersAreNonnullByDefault
public final class SpendCondition {

  public static @Nonnull WebElementsCondition spends(List<SpendJson> spends) {

    List<String> expected = convertToExpectedSpend(spends);

    return new WebElementsCondition() {
      @Override
      public String toString() {
        return expected.toString();
      }

      @Override
      public @Nonnull CheckResult check(Driver driver, List<WebElement> elements) {

        if (spends.isEmpty()) {
          throw new IllegalArgumentException("No expected spends given");
        }
        List<String> actual = convertToActualStat(elements);
        if (spends.size() != elements.size()) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)",
              spends.size(), elements.size());
          return rejected(message, actual.toString());
        }

        if (!actual.equals(expected)) {
          final String message = String.format("List spends mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual.toString());
        }
        return accepted();
      }

      @Override
      public void fail(CollectionSource collection, CheckResult lastCheckResult,
          @Nullable Exception cause, long timeoutMs) {
        try {
          Assertions.assertThat((String) lastCheckResult.getActualValue())
              .as("Все строки таблицы трат должны совпадать с ожидаемыми")
              .isEqualTo(toString());
        } catch (AssertionFailedError e) {
          throw new UIAssertionError(
              e.getMessage(),
              toString(), lastCheckResult.getActualValue()
          );
        }
      }
    };
  }

  private @Nonnull
  static List<String> convertToActualStat(List<WebElement> elements) {
    return elements
        .stream()
        .map(row -> {
              List<WebElement> cells = row.findElements(By.cssSelector("td"));
              return Stream.of(
                      cells.get(1),
                      cells.get(2),
                      cells.get(3),
                      cells.get(4)
                  )
                  .map(WebElement::getText)
                  .toList()
                  .toString();
            }
        )
        .toList();
  }

  private @Nonnull
  static List<String> convertToExpectedSpend(List<SpendJson> spends) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    DecimalFormat decimalFormat = new DecimalFormat("0.#");
    return spends.stream()
        .map(s -> List.of(
                s.category().name(),
                decimalFormat.format(s.amount()) + " " + s.currency().getValue(),
                s.description() == null ? "" : s.description(),
                dateFormat.format(s.spendDate())
            ).toString()
        ).toList();
  }
}
