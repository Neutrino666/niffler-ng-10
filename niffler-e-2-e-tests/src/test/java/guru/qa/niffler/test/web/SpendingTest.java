package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.auth.LoginPage;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      spendings = @Spending(
          category = "Учеба",
          amount = 89900,
          currency = CurrencyValues.RUB,
          description = "Обучение Niffler 2.0 юбилейный поток!"
      )
  )
  @Test
  void spendingDescriptionShouldBeEditedByTableAction(@Nonnull final UserJson user) {
    final String description = user.testData().spendings().getFirst().description();
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.testData().password())
        .editSpending(description)
        .setNewSpendingDescription(newDescription)
        .save()
        .checkThatTableContains(newDescription);
  }
}
