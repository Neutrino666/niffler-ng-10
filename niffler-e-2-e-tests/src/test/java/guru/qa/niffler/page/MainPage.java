package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.HeaderToolbar;
import lombok.Getter;

public class MainPage {

  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement statistics = $("#stat");
  @Getter
  private final HeaderToolbar headerToolbar = new HeaderToolbar();

  public MainPage checkThatPageLoaded() {
    spendingTable.should(visible);
    statistics.should(visible);
    return this;
  }

  public EditSpendingPage editSpending(String description) {
    spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContains(String description) {
    spendingTable.$$("tbody tr").find(text(description)).should(visible);
    return this;
  }
}
