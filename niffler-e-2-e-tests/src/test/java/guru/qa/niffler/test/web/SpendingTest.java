package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.auth.LoginPage;
import java.time.Instant;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Spendings")
@ParametersAreNonnullByDefault
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  private MainPage login(final UserJson user) {
    return Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.testData().password());
  }

  @User(
      spendings = @Spending(
          category = "Учеба",
          amount = 89900,
          currency = CurrencyValues.RUB,
          description = "Обучение Niffler 2.0 юбилейный поток!"
      )
  )
  @Test
  @DisplayName("Редактирование траты")
  void spendingDescriptionShouldBeEditedByTableAction(final UserJson user) {
    final String description = user.testData().spendings().getFirst().description();
    final String newDescription = "Обучение Niffler Next Generation";

    login(user)
        .getSpendingTable()
        .editSpending(description)
        .setNewSpendingDescription(newDescription)
        .save()
        .getSpendingTable()
        .checkThatTableContains(newDescription);
  }

  @User
  @Test
  @DisplayName("Добавление нового спендинга")
  void addNewSpending(final UserJson user) {
    final String description = RandomDataUtils.getRandomName();
    login(user)
        .getHeader()
        .addSpendingPge()
        .setAmount(1)
        .setNewCategory(RandomDataUtils.getRandomCategoryName())
        .setSpendingDate(Date.from(Instant.now()))
        .setNewSpendingDescription(description)
        .save()
        .getSpendingTable()
        .checkThatTableContains(description);
  }
}
