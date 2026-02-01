package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class SearchField extends BaseComponent<SearchField> {

  private final SelenideElement clearBtn = $("#input-clear");

  public SearchField() {
    super($("input[ aria-label = 'search' ]"));
  }

  public @Nonnull SearchField search(final String string) {
    self.setValue(string)
        .pressEnter();
    return this;
  }

  public @Nonnull SearchField clearIfNotEmpty() {
    String searchText = self.getValue();
    if (searchText != null && !searchText.isEmpty()) {
      clearBtn.click();
    }
    return this;
  }
}
