package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.components.Calendar;
import io.qameta.allure.Step;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class EditSpendingPage extends BasePage<EditSpendingPage> {

  public static final String URL = CFG.frontUrl() + "spending";

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement currencyInput = $("#currency");
  private final SelenideElement saveBtn = $("#save");
  private final ElementsCollection categories = $$(".MuiList-padding li");

  @Getter
  private final Calendar calendar = new Calendar();


  @Step("ЗЗапронение траты из json")
  @Nonnull
  public EditSpendingPage fillPage(SpendJson spend) {
    return setSpendingDate(spend.spendDate())
        .setAmount(spend.amount())
        .setCurrency(spend.currency())
        .setCategory(spend.category().name())
        .setSpendingDescription(spend.description());
  }

  @Step("Выбор валтюты траты: '{currency}'")
  @Nonnull
  public EditSpendingPage setCurrency(CurrencyValues currency) {
    currencyInput.setValue(currency.name());
    return this;
  }

  @Step("Ввод суммы траты '{amount}'")
  public @Nonnull EditSpendingPage setAmount(final Double amount) {
    amountInput.val(String.valueOf(amount));
    return this;
  }

  @Step("Ввод новой категории: '{name}'")
  public @Nonnull EditSpendingPage setCategory(final String name) {
    categoryInput.val(name);
    return this;
  }

  @Step("Выбор существующей категории: '{name}'")
  public @Nonnull EditSpendingPage selectExistCategory(final String name) {
    categories.find(text(name)).click();
    return this;
  }

  @Step("Ввод даты траты: '{date}'")
  public @Nonnull EditSpendingPage setSpendingDate(final Date date) {
    calendar.selectDateInCalendar(date);
    return this;
  }

  @Step("Ввод описания: '{description}' траты")
  public @Nonnull EditSpendingPage setSpendingDescription(final String description) {
    descriptionInput.val(description);
    return this;
  }

  @Step("Сохранение траты")
  public @Nonnull MainPage save() {
    saveBtn.click();
    return new MainPage();
  }
}
