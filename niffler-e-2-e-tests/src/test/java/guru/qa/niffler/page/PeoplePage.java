package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.SearchField;
import guru.qa.niffler.page.components.UsersHeader;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class PeoplePage extends BasePage<PeoplePage> {

  public static final String URL = CFG.frontUrl() + "people/all";

  private final SelenideElement tableRoot = $("#simple-tabpanel-all");
  private final ElementsCollection users = tableRoot.$$("tbody tr");

  @Getter
  private final SearchField searchField = new SearchField();

  @Getter
  private final UsersHeader usersHeader = new UsersHeader();

  @Step("Проверка прогрузки страницы")
  @Nonnull
  public PeoplePage checkThatPageLoaded() {
    tableRoot.shouldBe(visible);
    return this;
  }

  @Step("Отправляем запрос на добавление пользователя как друга: '{username}'")
  @Nonnull
  public PeoplePage sendFriendInvitationToUser(String username) {
    searchField.search(username);
    SelenideElement friendRow = users.find(text(username));
    friendRow.$(byText("Add friend")).click();
    return this;
  }

  @Step("Проверяем наличие исходящего запроса в друзья к: '{friend}'")
  public @Nonnull PeoplePage checkOutcomeInvitationIsVisible(final String friend) {
    searchField.search(friend);
    users.find(text(friend))
        .shouldBe(visible);
    return this;
  }

  @Step("Проверяем наличие пользователя: '{username}'")
  @Nonnull
  public PeoplePage checkExistingUser(String username) {
    searchField.search(username);
    users.find(text(username)).should(visible);
    return this;
  }
}
