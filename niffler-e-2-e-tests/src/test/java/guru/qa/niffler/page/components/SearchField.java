package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SearchField {

  private final SelenideElement searchInput = $("input[ aria-label = 'search' ]");
  private final SelenideElement clearBtn = $("#input-clear");

  public @Nonnull SearchField search(String string) {
    searchInput.setValue(string)
        .pressEnter();
    return this;
  }

  public @Nonnull SearchField clearIfNotEmpty() {
    String searchText = searchInput.getValue();
    if (searchText != null && !searchText.isEmpty()) {
      clearBtn.click();
    }
    return this;
  }
}
