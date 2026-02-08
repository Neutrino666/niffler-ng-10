package guru.qa.niffler.page;

import guru.qa.niffler.page.components.SpendingTable;
import guru.qa.niffler.page.components.StatComponent;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class MainPage extends BasePage<MainPage> {

  public static final String URL = CFG.frontUrl() + "main";

  @Getter
  private final SpendingTable spendingTable = new SpendingTable();
  @Getter
  private final StatComponent statComponent = new StatComponent();

  @Step("Проверка загрузки главной страницы")
  public @Nonnull MainPage checkThatPageLoaded() {
    spendingTable.checkThatPageLoaded();
    statComponent.checkThatComponentLoaded();
    return this;
  }

  @Step("Скриншот сравнение статистики")
  public @Nonnull MainPage assertStatisticScreen(BufferedImage expected) {
    statComponent.assertStatisticScreen(expected);
    return this;
  }

  @Step("Проверяем количество трат в статистике")
  public @Nonnull MainPage assertStatCount(Integer count) {
    statComponent.assertStatCount(count);
    return this;
  }
}
