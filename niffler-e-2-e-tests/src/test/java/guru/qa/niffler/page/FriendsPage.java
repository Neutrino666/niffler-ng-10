package guru.qa.niffler.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class FriendsPage {

  private final ElementsCollection tabs = $$(".MuiTab-textColorInherit");
  private final SelenideElement searchInput = $("input[aria-label='search']");
  private final SelenideElement searchBtn = $("#input-submit");
  private final ElementsCollection users = $$("tbody tr");
  private final ElementsCollection friends = $$("#friends tr");
  private final ElementsCollection requests = $$("#requests tr");
  private final ElementsCollection all = $$("#all tr");

  @Getter
  @ToString
  @RequiredArgsConstructor
  public enum Tab {
    FRIENDS("Friends"),
    ALL_PEOPLE("All people");
    private final String value;
  }

  @Step("Открываем вкладку друзей '{tab}'")
  public void switchTab(@NonNull final Tab tab) {
    SelenideElement el = tabs.find(text(tab.getValue())).as(tab.getValue());
    el.click();
    el.shouldHave(attribute("aria-selected", "true"));
  }

  @Step("Проверяем отсутствие друзей")
  public FriendsPage checkFriendsNotExist() {
    users.shouldHave(size(0));
    return this;
  }

  @Step("Проверяем наличие друга: '{friend}'")
  public FriendsPage checkFriendIsVisible(@NonNull final String friend) {
    friends.find(text(friend)).shouldBe(visible);
    return this;
  }

  @Step("Проверяем наличие входящего запроса в друзья от: '{friend}'")
  public FriendsPage checkIncomeInvitationIsVisible(@NonNull final String friend) {
    requests.find(text(friend)).shouldBe(visible);
    return this;
  }

  @Step("Проверяем наличие исходящего запроса в друзья к: '{friend}'")
  public FriendsPage checkOutcomeInvitationIsVisible(@NonNull final String friend) {
    switchTab(Tab.ALL_PEOPLE);
    all.find(text(friend)).shouldBe(visible);
    return this;
  }
}
