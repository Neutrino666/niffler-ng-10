package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SearchField;
import guru.qa.niffler.page.components.UsersHeader;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public class PeoplePage {

  private final SelenideElement tableRoot = $("#simple-tabpanel-all");
  private final ElementsCollection users = tableRoot.$$("tbody tr");

  @Getter
  private final Header header = new Header();

  @Getter
  private final SearchField searchField = new SearchField();

  @Getter
  private final UsersHeader usersHeader = new UsersHeader();

  @Step("Проверяем наличие исходящего запроса в друзья к: '{friend}'")
  public @Nonnull PeoplePage checkOutcomeInvitationIsVisible(final String friend) {
    searchField.search(friend);
    users.find(text(friend))
        .shouldBe(visible);
    return this;
  }
}
