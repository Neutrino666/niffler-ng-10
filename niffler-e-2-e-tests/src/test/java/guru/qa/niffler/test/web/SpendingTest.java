package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Spendings")
@ParametersAreNonnullByDefault
public final class SpendingTest {

  @User(
      spendings = @Spending(
          category = "Учеба",
          amount = 89900,
          currency = CurrencyValues.RUB,
          description = "Обучение Niffler 2.0 юбилейный поток!"
      )
  )
  @ApiLogin
  @Test
  @DisplayName("Редактирование траты")
  void spendingDescriptionShouldBeEditedByTableAction(final UserJson user) {
    final String description = user.testData().spendings().getFirst().description();
    final String newDescription = "Обучение Niffler Next Generation";
    new MainPage()
        .getSpendingTable()
        .editSpending(description)
        .setNewSpendingDescription(newDescription)
        .save()
        .getSpendingTable()
        .checkThatTableContains(newDescription);
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Добавление нового спендинга")
  void addNewSpending() {
    final String description = RandomDataUtils.getRandomName();
    Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
        .setAmount(1)
        .setNewCategory(RandomDataUtils.getRandomCategoryName())
        .setSpendingDate(Date.from(Instant.now()))
        .setNewSpendingDescription(description)
        .save()
        .getSpendingTable()
        .checkThatTableContains(description);
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Уведомление добавления нового спендинга")
  void addNewSpendingSnackbar() {
    final String description = RandomDataUtils.getRandomName();
    Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
        .setAmount(1)
        .setNewCategory(RandomDataUtils.getRandomCategoryName())
        .setSpendingDate(Date.from(Instant.now()))
        .setNewSpendingDescription(description)
        .save()
        .checkSnackbarText("New spending is successfully created");
  }

  @User(
      spendings = @Spending(
          category = "Учеба",
          amount = 89900,
          currency = CurrencyValues.RUB,
          description = "Обучение Niffler 2.0 юбилейный поток!"
      )
  )
  @ApiLogin
  @Test
  @DisplayName("Редактирование траты")
  void editedSpendingSnackbar(final UserJson user) {
    final String description = user.testData().spendings().getFirst().description();
    final String newDescription = "Обучение Niffler Next Generation";
    new MainPage()
        .getSpendingTable()
        .editSpending(description)
        .setNewSpendingDescription(newDescription)
        .save()
        .checkSnackbarText("Spending is edited successfully");
  }

  @User(
      spendings = @Spending(
          category = "Учеба",
          amount = 666,
          currency = CurrencyValues.RUB,
          description = "Обучение Niffler 2.0 юбилейный поток!"
      )
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat.png")
  @DisplayName("SCREEN Сравнение создания траты")
  void checkStatComponentTest(BufferedImage expected) {
    new MainPage()
        .assertStatisticScreen(expected)
        .getStatComponent()
        .checkStatBubbles(new Bubble(Color.YELLOW, "Учеба 666 ₽"))
        .checkBubbles(Color.YELLOW);
  }

  @User(
      spendings = {
          @Spending(
              category = "Учеба",
              amount = 666,
              currency = CurrencyValues.RUB,
              description = "Обучение Niffler 2.0 юбилейный поток!"
          ),
          @Spending(
              category = "Театр",
              amount = 1000,
              currency = CurrencyValues.RUB,
              description = "Дж. Верди 'Аида'"
          )
      }
  )
  @ApiLogin
  @ScreenShotTest(value = "img/remove-stat.png")
  @DisplayName("SCREEN Сравнение удаление траты")
  void checkRemoveStatComponentTest(final UserJson user, BufferedImage expected) {
    List<SpendJson> spends = user.testData()
        .spendings();
    String description = spends.get(0)
        .description();
    new MainPage()
        .assertStatCount(spends.size())
        .getSpendingTable()
        .deleteSpending(description)
        .getHeader()
        .toMainPage()
        .assertStatisticScreen(expected)
        .assertStatCount(spends.size() - 1);
  }

  @User(
      spendings = {
          @Spending(
              category = "Учеба",
              amount = 90000,
              currency = CurrencyValues.RUB,
              description = "Обучение Niffler 2.0 юбилейный поток!"
          ),
          @Spending(
              category = "Театр",
              amount = 1000,
              currency = CurrencyValues.RUB,
              description = "Дж. Верди 'Аида'"
          )
      }
  )
  @ApiLogin
  @ScreenShotTest(value = "img/archive-stat.png")
  @DisplayName("SCREEN Сравнение отображения архивных траты")
  void checkArchiveStatComponentTest(final UserJson user, BufferedImage expected) {
    List<SpendJson> spends = user.testData()
        .spendings();
    String category = spends.get(0)
        .category()
        .name();
    new MainPage()
        .assertStatCount(spends.size())
        .getHeader()
        .toProfilePage()
        .clickArchive(category)
        .acceptToArchive()
        .getHeader()
        .toMainPage()
        .assertStatisticScreen(expected)
        .assertStatCount(spends.size());
  }


  @User(
      spendings = @Spending(
          category = "Учеба",
          amount = 10000,
          currency = CurrencyValues.RUB,
          description = "Обучение Niffler 2.0 юбилейный поток!"
      )
  )
  @ApiLogin
  @ScreenShotTest(value = "img/edited-stat.png")
  @DisplayName("SCREEN Редактирование траты")
  void editedStatComponent(final UserJson user, BufferedImage expected) {
    final String description = user.testData().spendings().getFirst().description();
    final String newDescription = "Обучение Niffler Next Generation";
    List<SpendJson> spends = user.testData()
        .spendings();
    new MainPage()
        .assertStatCount(spends.size())
        .getSpendingTable()
        .editSpending(description)
        .setNewSpendingDescription(newDescription)
        .setAmount(100500)
        .save()
        .assertStatCount(spends.size())
        .assertStatisticScreen(expected);
  }

  @User(
      spendings = {
          @Spending(
              category = "Учеба",
              amount = 666,
              currency = CurrencyValues.RUB,
              description = "Обучение Niffler 2.0 юбилейный поток!"
          ),
          @Spending(
              category = "Театр",
              amount = 1000,
              currency = CurrencyValues.RUB,
              description = "Дж. Верди 'Риголетто'"
          )
      }
  )
  @Test
  @DisplayName("Сравнение наполнения таблицы трат")
  void checkSpendTable(final UserJson user) {
    new MainPage()
        .getSpendingTable()
        .assertSpends(user.testData().spendings());
  }
}
