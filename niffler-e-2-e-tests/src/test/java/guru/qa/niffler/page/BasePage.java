package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.ScreenDiffResult;
import guru.qa.niffler.page.components.Header;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import lombok.Getter;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();

  @Getter
  protected final Header header = new Header();
  protected final SelenideElement snackbar = $(".MuiAlert-message");

  @SuppressWarnings("unchecked")
  public T checkSnackbarText(String text) {
    snackbar.shouldHave(text(text));
    return (T) this;
  }

  protected void assertScreen(BufferedImage expected, SelenideElement actualLocator) {
    assertScreen(expected, actualLocator, 0);
  }

  protected void assertScreen(BufferedImage expected, SelenideElement actualLocator,
      Integer waitMills) {
    Selenide.sleep(waitMills);
    try {
      BufferedImage actual = ImageIO.read(Objects.requireNonNull(
              $(actualLocator).screenshot()
          )
      );
      assertThat(new ScreenDiffResult(
          expected, actual
      ).getAsBoolean())
          .describedAs(
              "Отличия в скриншоте не превышают допустимую погрешность: %spx",
              ScreenDiffResult.getALLOWED_DIFF_PIXELS()
          )
          .isFalse();
    } catch (IOException e) {
      throw new RuntimeException("Screen comparison failure: " + e);
    }
  }
}
