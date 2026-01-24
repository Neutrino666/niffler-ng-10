package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  @Getter
  protected final Header header = new Header();
  protected final SelenideElement snackbar = $(".MuiAlert-message");

  @SuppressWarnings("unchecked")
  public T checkSnackbarText(String text) {
    snackbar.shouldHave(text(text));
    return (T) this;
  }
}
