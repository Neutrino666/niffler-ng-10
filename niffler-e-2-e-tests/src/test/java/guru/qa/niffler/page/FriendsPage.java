package guru.qa.niffler.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.ConfirmDialog;
import guru.qa.niffler.page.components.SearchField;
import guru.qa.niffler.page.components.UsersHeader;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class FriendsPage extends BasePage<FriendsPage> {

  public static final String URL = CFG.frontUrl() + "people/friends";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");

  private final SelenideElement tableRoot = $("#simple-tabpanel-friends");
  private final ElementsCollection users = tableRoot.$$("tbody tr");
  private final ElementsCollection friends = tableRoot.$$("#friends tr");
  private final ElementsCollection requests = tableRoot.$$("#requests tr");

  @Getter
  private final SearchField searchField = new SearchField();

  @Getter
  private final UsersHeader usersHeader = new UsersHeader();

  @Step("Проверка загрузки страницы с пользователями")
  public @Nonnull FriendsPage checkThatPageLoaded() {
    peopleTab.shouldBe(visible);
    allTab.shouldBe(visible);
    return this;
  }

  @Step("Проверяем количество существующих пользователей")
  @Nonnull
  public FriendsPage checkExistingFriendsCount(int expectedCount) {
    users.shouldHave(size(expectedCount));
    return this;
  }

  @Step("Проверяем отсутствие друзей")
  public @Nonnull FriendsPage checkFriendsNotExist() {
    users.shouldHave(size(0));
    return this;
  }

  @Step("Проверяем наличие друга: '{friend}'")
  public @Nonnull FriendsPage checkFriendIsVisible(final String friend) {
    research(friend);
    friends.find(text(friend))
        .shouldBe(visible);
    return this;
  }

  @Step("Проверяем наличие входящего запроса в друзья от: '{friend}'")
  public FriendsPage checkIncomeInvitationIsVisible(final String friend) {
    research(friend);
    requests.find(text(friend))
        .shouldBe(visible);
    return this;
  }

  @Step("Прием заявки в друзья")
  public @Nonnull FriendsPage acceptFriends(final String... friends) {
    for (String friend : friends) {
      research(friend);
      SelenideElement user = requests.find(text(friend));
      SelenideElement acceptBtn = acceptBtnByRequestRow(user);
      acceptBtn.click();
      acceptBtn.shouldNotBe(exist);
    }
    return this;
  }

  @Step("Отклонение заявки в друзья")
  public @Nonnull FriendsPage declineFriends(final String... friends) {
    for (@Nonnull String friend : friends) {
      research(friend);
      SelenideElement user = requests.find(text(friend));
      SelenideElement declineBtn = declineBtnByRequestRow(user);
      declineBtn.click();
      new ConfirmDialog().checkThatPageLoaded()
          .clickButtonByText("Decline");
      declineBtn.shouldNotBe(exist);
    }
    return this;
  }

  private void research(final String username) {
    searchField.clearIfNotEmpty()
        .search(username);
  }

  private @Nonnull SelenideElement acceptBtnByRequestRow(final SelenideElement request) {
    return request.$$("button")
        .find(text("Accept"));
  }

  private @Nonnull SelenideElement declineBtnByRequestRow(final SelenideElement request) {
    return request.$$("button")
        .find(text("Decline"));
  }
}
