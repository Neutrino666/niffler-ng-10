package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfirmDialog {

  private final SelenideElement root = $("*[ role = 'dialog' ]");
  private final SelenideElement title = root.$(".MuiDialogTitle-root");
  private final ElementsCollection buttons = root.$$("button");

  public @Nonnull ConfirmDialog checkThatPageLoaded() {
    root.should(visible);
    return this;
  }

  public void delete() {
    buttons.find(text("Delete")).click();
    root.shouldNotBe(visible);
  }

  public void cancel() {
    buttons.find(text("Cancel")).click();
    root.shouldNotBe(visible);
  }

  public void decline() {
    buttons.find(text("Decline")).click();
    root.shouldNotBe(visible);
  }

  public void close() {
    buttons.find(text("Close")).click();
    root.shouldNotBe(visible);
  }
}
