package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.PeoplePage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ParametersAreNonnullByDefault
public class UsersHeader extends BaseComponent<UsersHeader> {

  private final ElementsCollection tabs = self.$$(".MuiTab-textColorInherit");

  public UsersHeader() {
    super($("div[ role = 'navigation' ]"));
  }

  public @Nonnull PeoplePage toAllPeoplePage() {
    switchTab(Tab.ALL_PEOPLE);
    return new PeoplePage();
  }

  public @Nonnull FriendsPage toFriendsPage() {
    switchTab(Tab.FRIENDS);
    return new FriendsPage();
  }

  @Step("Переход на вкладку: '{tab}'")
  private void switchTab(final Tab tab) {
    SelenideElement el = tabs.find(text(tab.getValue()))
        .as(tab.getValue());
    el.click();
    el.shouldHave(attribute("aria-selected", "true"));
  }

  @Getter
  @ToString
  @RequiredArgsConstructor
  private enum Tab {
    FRIENDS("Friends"),
    ALL_PEOPLE("All people");

    @Nonnull
    private final String value;
  }
}
