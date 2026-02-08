package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.helpers.ScreenDiffResult;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

  protected final SelenideElement self;

  public BaseComponent(SelenideElement self) {
    this.self = self;
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
      throw new RuntimeException(e);
    }
  }
}
