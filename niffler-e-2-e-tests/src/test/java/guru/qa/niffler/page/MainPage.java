package guru.qa.niffler.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.SpendingTable;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class MainPage extends BasePage<MainPage> {

  private final SelenideElement selfStat = $("#stat");
  private final SelenideElement statCanvas = selfStat.$("canvas[ role = 'img' ]");

  private final ElementsCollection statRows = selfStat.$$("ul li");

  @Getter
  private final SpendingTable spendingTable = new SpendingTable();

  @Step("Проверка загрузки главной страницы")
  public @Nonnull MainPage checkThatPageLoaded() {
    spendingTable.checkThatPageLoaded();
    selfStat.should(visible);
    return this;
  }

  @Step("Скриншот сравнение статистики")
  public @Nonnull MainPage assertStatisticScreen(BufferedImage expected) {
    assertScreen(expected, statCanvas, 4000);
    return this;
  }

  @Step("Проверяем количество трат в статистике")
  public @Nonnull MainPage assertStatCount(Integer count) {
    statRows.shouldHave(size(count));
    return this;
  }
}
