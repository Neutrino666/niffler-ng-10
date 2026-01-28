package guru.qa.niffler.page.components;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.colors;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

  private final SelenideElement statCanvas = self.$("canvas[ role = 'img' ]");

  private final ElementsCollection statRows = self.$$("ul li");

  public StatComponent() {
    super($("#stat"));
  }

  public @Nonnull StatComponent checkThatComponentLoaded() {
    self.should(visible);
    return this;
  }

  public @Nonnull StatComponent assertStatisticScreen(BufferedImage expected) {
    assertScreen(expected, statCanvas, 4000);
    return this;
  }

  public @Nonnull StatComponent assertStatCount(Integer count) {
    statRows.shouldHave(size(count));
    return this;
  }

  @Step("Проверям цвет первой траты в статистике")
  public @Nonnull StatComponent checkBubbles(Color... expectedColors) {
    statRows.shouldHave(colors(expectedColors));
    return this;
  }
}
