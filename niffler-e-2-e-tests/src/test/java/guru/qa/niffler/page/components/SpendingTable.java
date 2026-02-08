package guru.qa.niffler.page.components;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.SpendCondition.spends;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class SpendingTable extends BaseComponent<SpendingTable> {

  private final SelenideElement periodDropdown = self.$("#period");
  private final SelenideElement deleteBtn = self.$("#delete");
  private final ElementsCollection spendings = self.$$("tbody tr");

  private final ElementsCollection menuPeriod = $$("#menu-period li");

  @Getter
  private final Header header = new Header();

  @Getter
  private final SearchField searchField = new SearchField();

  public SpendingTable() {
    super($("#spendings"));
  }

  public @Nonnull SpendingTable checkThatPageLoaded() {
    self.should(visible);
    return this;
  }

  @Step("Выбор периода")
  public @Nonnull SpendingTable selectPeriod(final DataFilterValues period) {
    periodDropdown.click();
    menuPeriod.find(text(period.getValue())).click();
    periodDropdown.shouldHave(text(period.getValue()));
    return this;
  }

  @Step("Переход в редактирование траты: '{description}'")
  public @Nonnull EditSpendingPage editSpending(final String description) {
    spendings.find(text(description))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  @Step("Удаление траты: '{description}'")
  public @Nonnull SpendingTable deleteSpending(final String description) {
    setStateChxSpendingContains(true, description);
    deleteBtn.click();
    new ConfirmDialog().checkThatPageLoaded()
        .clickButtonByText("Delete");
    return this;
  }

  @Step("Поиск траты: '{description}'")
  public @Nonnull SpendingTable searchSpendingByDescription(final String description) {
    searchField.search(description);
    return this;
  }

  @Step("Проверка наличия трат: '{descriptions}'")
  public @Nonnull SpendingTable checkThatTableContains(final String... descriptions) {
    for (String description : descriptions) {
      searchField.search(description);
      spendings.find(text(description))
          .should(visible);
    }
    return this;
  }

  @Step("Проверка размера таблицы ожидается: '{expectedSize}'")
  public @Nonnull SpendingTable checkTableSize(final int expectedSize) {
    spendings.shouldHave(size(expectedSize));
    return this;
  }

  @Step("Проверка наполнения таблицы")
  public @Nonnull SpendingTable assertSpends(final List<SpendJson> expectedSpends) {
    spendings.shouldHave(spends(expectedSpends));
    return this;
  }

  private @Nonnull SpendingTable setStateChxSpendingContains(
      final boolean state,
      final String... strings) {
    for (String s : strings) {
      searchField.search(s);
      SelenideElement chx = spendings.find(text(s))
          .$("input");
      if (chx.isSelected() != state) {
        chx.click();
        chx.shouldBe(state ? selected : not(selected));
      }
    }
    searchField.clearIfNotEmpty();
    return this;
  }
}
