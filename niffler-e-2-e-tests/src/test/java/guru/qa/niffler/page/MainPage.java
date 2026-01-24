package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SpendingTable;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

  private final SelenideElement statistics = $("#stat");

  @Getter
  private final Header header = new Header();

  @Getter
  private final SpendingTable spendingTable = new SpendingTable();

  @Step("Проверка загрузки главной страницы")
  public @Nonnull MainPage checkThatPageLoaded() {
    spendingTable.checkThatPageLoaded();
    statistics.should(visible);
    return this;
  }
}
