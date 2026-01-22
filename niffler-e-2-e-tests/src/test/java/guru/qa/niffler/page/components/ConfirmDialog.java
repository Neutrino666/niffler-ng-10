package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class ConfirmDialog extends BaseComponent<ConfirmDialog> {

  private final SelenideElement title = self.$(".MuiDialogTitle-root");
  private final ElementsCollection buttons = self.$$("button");

  public ConfirmDialog() {
    super($("*[ role = 'dialog' ]"));
  }

  public @Nonnull ConfirmDialog checkThatPageLoaded() {
    self.should(visible);
    return this;
  }

  public void clickButtonByText(String text) {
    buttons.find(text(text))
        .click();
    self.shouldNotBe(visible);
  }
}
